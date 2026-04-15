package edu.comillas.icai.gitt.pat.spring.jpa3.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esDueno(#id, authentication.name)")
    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esDueno(#id, authentication.name)")
    @PatchMapping("/{id}")
    public Usuario patch(@PathVariable Long id, @RequestBody Usuario cambios) {
        return usuarioService.patch(id, cambios);
    }
}


