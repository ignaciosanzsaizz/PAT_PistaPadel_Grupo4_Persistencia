package edu.comillas.icai.gitt.pat.spring.jpa3.security;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String roleName = "USER";
        if (usuario.rol != null && usuario.rol.nombreRol != null && !usuario.rol.nombreRol.isBlank()) {
            roleName = usuario.rol.nombreRol.toUpperCase();
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5);
            }
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + roleName)
        );

        return User.withUsername(usuario.email)
                .password(usuario.password)
                .authorities(authorities)
                .accountLocked(Boolean.FALSE.equals(usuario.activo))
                .disabled(Boolean.FALSE.equals(usuario.activo))
                .build();
    }
}

