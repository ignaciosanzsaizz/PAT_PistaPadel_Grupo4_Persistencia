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
    @ResponseStatus(HttpStatus.CREATED) // If it works, return HTTP 201 CREATED
    public Usuario register(@RequestBody Usuario usuario) {

        // @RequestBody takes the JSON body and converts it into a Usuario object.
        // Then we send that Usuario to the service.
        // The service will validate/save/register the user.
        return usuarioService.registrar(usuario);
    }

    @GetMapping("/me")
    public Usuario me(Authentication authentication) { // me = method name, you choose this name
        return usuarioService.obtenerPorEmail(authentication.getName());
    }
}


