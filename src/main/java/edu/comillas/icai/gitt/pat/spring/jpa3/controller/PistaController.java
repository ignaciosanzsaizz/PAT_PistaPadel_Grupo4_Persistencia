package edu.comillas.icai.gitt.pat.spring.jpa3.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.PistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pistaPadel/courts")
public class PistaController {
    
    @Autowired
    private PistaService pistaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Pista crear(@RequestBody Pista pista) {
        return pistaService.crearPista(pista);
    }

    @GetMapping
    public List<Pista> listar() {
        return pistaService.listarPistas();
    }

    @GetMapping("/{id}")
    public Pista obtener(@PathVariable Long id) {
        return pistaService.buscarPista(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Pista actualizar(@PathVariable Long id, @RequestBody Pista cambios) {
        return pistaService.actualizarPista(id, cambios);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        pistaService.borrarPista(id);
    }
}


