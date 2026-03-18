package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.EmailService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    ReservaRepository reservaRepository;

    @Mock
    EmailService emailService; // También mockeamos el email para que no envíe correos reales

    @InjectMocks
    ReservaService reservaService;

    @Test
    void crearReserva() {
        // GIVEN: Preparamos los datos
        Reserva reserva = new Reserva();
        reserva.usuario = new Usuario();
        reserva.usuario.email = "test@test.com";
        reserva.horaInicio = LocalTime.of(10, 0);
        reserva.duracionMinutos = 60;

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // WHEN: Ejecutamos el método del servicio
        Reserva resultado = reservaService.crearReserva(reserva);

        // THEN: Verificamos resultados y comportamiento
        assertNotNull(resultado);
        assertEquals(LocalTime.of(11, 0), resultado.horaFin); // Verificamos que se calculó la hora fin
        verify(reservaRepository, times(1)).save(reserva); // Verificamos que se llamó al repo
        verify(emailService, times(1)).enviarConfirmacion(any()); // Verificamos que se intentó enviar email
    }
}