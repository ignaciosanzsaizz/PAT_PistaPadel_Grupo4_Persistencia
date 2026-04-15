package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.controller;
import edu.comillas.icai.gitt.pat.spring.jpa3.controller.AuthController;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTest {

    private MockMvc mockMvc;
    
    @Mock
    private UsuarioService usuarioService;
    
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_devuelve201_y_usuario_creado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.id = 1L;
        usuario.nombre = "Ana";
        usuario.apellidos = "García";
        usuario.email = "ana@test.com";
        usuario.password = "1234";
        usuario.telefono = "123456789";
        usuario.activo = true;

        when(usuarioService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/pistaPadel/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellidos": "García",
                                  "email": "ana@test.com",
                                  "password": "1234",
                                  "telefono": "123456789",
                                  "activo": true
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@test.com"));
    }
}