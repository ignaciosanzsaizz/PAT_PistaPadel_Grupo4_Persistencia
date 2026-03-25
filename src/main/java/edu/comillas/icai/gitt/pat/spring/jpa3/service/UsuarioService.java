package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Rol;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.RolRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private static final String ROL_POR_DEFECTO = "USER";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body de usuario requerido");
        }

        usuario.email = normalizar(usuario.email);
        usuario.nombre = normalizar(usuario.nombre);
        usuario.apellidos = normalizar(usuario.apellidos);
        usuario.telefono = normalizar(usuario.telefono);

        log.debug("Intentando registrar usuario con email: {}", usuario.email);

        if (isBlank(usuario.nombre) || isBlank(usuario.apellidos) || isBlank(usuario.email)
                || isBlank(usuario.password) || isBlank(usuario.telefono)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nombre, apellidos, email, password y telefono son obligatorios");
        }

        if (usuarioRepository.findByEmail(usuario.email).isPresent()) {
            log.error("Error al registrar usuario: El email {} ya existe", usuario.email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        if (!usuario.password.startsWith("{")) {
            usuario.password = passwordEncoder.encode(usuario.password);
        }

        if (usuario.rol == null || isBlank(usuario.rol.nombreRol)) {
            usuario.rol = obtenerOCrearRol(ROL_POR_DEFECTO);
        } else {
            usuario.rol = obtenerOCrearRol(usuario.rol.nombreRol);
        }

        if (usuario.fechaRegistro == null) {
            usuario.fechaRegistro = new Date();
        }
        if (usuario.activo == null) {
            usuario.activo = true;
        }

        try {
            Usuario guardado = usuarioRepository.save(usuario);
            log.info("Usuario {} registrado correctamente", guardado.nombre);
            return guardado;
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al registrar usuario {}", usuario.email, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Datos de registro invalidos o incompletos", e);
        }
    }

    private Rol obtenerOCrearRol(String nombreRol) {
        String nombreNormalizado = normalizar(nombreRol);
        if (isBlank(nombreNormalizado)) {
            nombreNormalizado = ROL_POR_DEFECTO;
        }
        nombreNormalizado = nombreNormalizado.toUpperCase(Locale.ROOT);
        final String nombreRolFinal = nombreNormalizado;

        return rolRepository.findByNombreRol(nombreRolFinal)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.nombreRol = nombreRolFinal;
                    nuevoRol.descripcion = "Rol creado automaticamente";
                    return rolRepository.save(nuevoRol);
                });
    }

    private static String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
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