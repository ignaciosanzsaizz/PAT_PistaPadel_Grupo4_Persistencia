package edu.comillas.icai.gitt.pat.spring.jpa3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Nuevos usuarios: bcrypt. Usuarios legacy sin prefijo: fallback temporal a texto plano.
        var delegating = (org.springframework.security.crypto.password.DelegatingPasswordEncoder)
                PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegating.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return delegating;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // API stateless: evita 403 por CSRF en llamadas POST/PATCH/DELETE desde Postman
        http.csrf(csrf -> csrf.disable());

        // 2. Permitimos que los iframes de H2 se muestren en la misma pantalla
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3. Dejamos entrar a H2 y al registro público; lo demás requiere autenticación
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/pistaPadel/auth/register", "/pistaPadel/auth/login", "/error").permitAll()
                .anyRequest().authenticated()
        );

        // Para clientes REST (Postman) devolvemos 401/403 en lugar de HTML de login
        http.formLogin(form -> form.disable());
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}