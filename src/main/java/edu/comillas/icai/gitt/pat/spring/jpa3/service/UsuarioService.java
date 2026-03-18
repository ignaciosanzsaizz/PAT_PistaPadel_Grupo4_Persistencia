package edu.comillas.icai.gitt.pat.spring.jpa3.service;


import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        log.debug("Intentando crear usuario con email: {}", usuario.email);
        if (usuarioRepository.findByEmail(usuario.email).isPresent()) {
            log.error("Error al crear usuario: El email {} ya existe", usuario.email);
            throw new RuntimeException("El usuario ya existe");
        }
        log.info("Usuario {} creado correctamente", usuario.nombre);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}