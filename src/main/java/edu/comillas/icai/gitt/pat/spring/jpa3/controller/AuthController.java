package edu.comillas.icai.gitt.pat.spring.jpa3.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario register(@RequestBody Usuario usuario) {
        return usuarioService.registrar(usuario);
    }

    @GetMapping("/me")
    public Usuario me(Authentication authentication) {
        return usuarioService.obtenerPorEmail(authentication.getName());
    }
}


