package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.service;


import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    UsuarioService usuarioService;

    @Test
    void crearUsuario_Exito() {
        Usuario usuario = new Usuario();
        usuario.email = "nuevo@test.com";

        when(usuarioRepository.findByEmail(usuario.email)).thenReturn(Optional.empty());
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.crearUsuario(usuario);

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void crearUsuario_ErrorSiEmailExiste() {
        Usuario usuario = new Usuario();
        usuario.email = "existe@test.com";

        when(usuarioRepository.findByEmail(usuario.email)).thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(usuario);
        });
    }
}