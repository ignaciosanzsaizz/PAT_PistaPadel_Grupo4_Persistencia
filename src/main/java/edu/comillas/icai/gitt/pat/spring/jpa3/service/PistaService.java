package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PistaService {
    private static final Logger log = LoggerFactory.getLogger(PistaService.class);
    
    @Autowired
    private PistaRepository pistaRepository;

    @Transactional
    public Pista crearPista(Pista pista) {
        log.info("Creando nueva pista: {}", pista.nombre);
        return pistaRepository.save(pista);
    }

    @Transactional
    public Pista guardarPista(Pista pista) {
        return crearPista(pista);
    }

    public List<Pista> listarPistas() {
        log.debug("Listando todas las pistas del sistema");
        return pistaRepository.findAll();
    }

    public List<Pista> listarPistasActivas() {
        log.debug("Listando todas las pistas activas del sistema");
        return pistaRepository.findAll().stream()
                .filter(p -> p.activa)
                .toList();
    }

    public Pista buscarPista(Long id) {
        log.debug("Buscando pista por ID: {}", id);
        return pistaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pista no encontrada"
                ));
    }

    @Transactional
    public Pista actualizarPista(Long id, Pista cambios) {
        log.debug("Actualizando pista ID: {}", id);
        Pista pista = buscarPista(id);

        if (cambios.nombre != null && !cambios.nombre.isBlank()) {
            pista.nombre = cambios.nombre;
        }
        if (cambios.ubicacion != null && !cambios.ubicacion.isBlank()) {
            pista.ubicacion = cambios.ubicacion;
        }
        if (cambios.precioHora != null) {
            pista.precioHora = cambios.precioHora;
        }
        if (cambios.activa != null) {
            pista.activa = cambios.activa;
        }

        log.info("Pista ID {} actualizada correctamente", id);
        return pistaRepository.save(pista);
    }

    @Transactional
    public void borrarPista(Long id) {
        log.debug("Eliminando pista ID: {}", id);
        Pista pista = buscarPista(id);
        pistaRepository.delete(pista);
        log.info("Pista ID {} eliminada correctamente", id);
    }
}
