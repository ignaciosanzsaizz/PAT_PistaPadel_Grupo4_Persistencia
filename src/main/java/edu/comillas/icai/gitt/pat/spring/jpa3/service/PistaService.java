package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PistaService {
    private static final Logger log = LoggerFactory.getLogger(PistaService.class);
    private final PistaRepository pistaRepository;

    public PistaService(PistaRepository pistaRepository) {
        this.pistaRepository = pistaRepository;
    }

    public Pista guardarPista(Pista pista) {
        log.info("Guardando nueva pista: {}", pista.nombre);
        return pistaRepository.save(pista);
    }

    public List<Pista> listarPistasActivas() {
        log.debug("Listando todas las pistas del sistema");
        return pistaRepository.findAll().stream()
                .filter(p -> p.activa)
                .toList();
    }
}
