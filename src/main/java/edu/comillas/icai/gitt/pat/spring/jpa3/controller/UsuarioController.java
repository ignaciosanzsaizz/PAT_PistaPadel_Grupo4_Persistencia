package edu.comillas.icai.gitt.pat.spring.jpa3.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Regla: solo ADMIN lista usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    // (Recomendado) ADMIN o dueño puede ver
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esDueno(#id, authentication.name)")
    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    // Regla: solo dueño o ADMIN puede modificar
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esDueno(#id, authentication.name)")
    @PatchMapping("/{id}")
    public Usuario patch(@PathVariable Long id, @RequestBody Usuario cambios) {
        return usuarioService.patch(id, cambios);
    }
}


