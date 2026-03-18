package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class  PistaRepositoryTest {

    @Autowired
    private PistaRepository pistaRepository;

    @Test
    @DisplayName("Debe buscar pista por nombre")
    void buscarPistaPorNombre() {
        Pista pista = new Pista();
        pista.nombre = "Central";
        pista.ubicacion = "Barcelona";
        pista.precioHora = 25.0;
        pista.activa = true;
        pista.fechaAlta = new Date();
        pistaRepository.save(pista);

        Optional<Pista> resultado = pistaRepository.findByNombre("Central");

        assertTrue(resultado.isPresent());
        assertEquals("Barcelona", resultado.get().ubicacion);
    }
}