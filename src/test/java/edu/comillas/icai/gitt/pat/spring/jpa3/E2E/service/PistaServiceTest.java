package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.service;


import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.PistaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PistaServiceTest {

    @Mock
    PistaRepository pistaRepository;

    @InjectMocks
    PistaService pistaService;

    @Test
    void listarPistasActivas() {
        Pista p1 = new Pista(); p1.activa = true;
        Pista p2 = new Pista(); p2.activa = false;

        when(pistaRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Pista> activas = pistaService.listarPistasActivas();

        assertEquals(1, activas.size());
    }
}