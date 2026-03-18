package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario registrar(Usuario usuario) {
        log.debug("Intentando registrar usuario con email: {}", usuario.email);
        if (usuarioRepository.findByEmail(usuario.email).isPresent()) {
            log.error("Error al registrar usuario: El email {} ya existe", usuario.email);
            throw new RuntimeException("El usuario ya existe");
        }
        log.info("Usuario {} registrado correctamente", usuario.nombre);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        return registrar(usuario);
    }

    public Usuario obtenerPorEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obtenerPorId(Long id) {
        log.debug("Buscando usuario por ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    public List<Usuario> listar() {
        log.debug("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarTodos() {
        return listar();
    }

    @Transactional
    public Usuario patch(Long id, Usuario cambios) {
        log.debug("Actualizando usuario ID: {}", id);
        Usuario usuario = obtenerPorId(id);

        if (cambios.nombre != null && !cambios.nombre.isBlank()) {
            usuario.nombre = cambios.nombre;
        }
        if (cambios.apellidos != null && !cambios.apellidos.isBlank()) {
            usuario.apellidos = cambios.apellidos;
        }
        if (cambios.telefono != null && !cambios.telefono.isBlank()) {
            usuario.telefono = cambios.telefono;
        }

        log.info("Usuario ID {} actualizado correctamente", id);
        return usuarioRepository.save(usuario);
    }

    public boolean esDueno(Long id, String email) {
        log.debug("Verificando si el usuario con email {} es dueño del usuario ID {}", email, id);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return false;
        }
        return id.equals(usuario.get().id);
    }
}