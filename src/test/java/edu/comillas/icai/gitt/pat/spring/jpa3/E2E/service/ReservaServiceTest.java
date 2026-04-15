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
    EmailService emailService;

    @InjectMocks
    ReservaService reservaService;

    @Test
    void crearReserva() {
        Reserva reserva = new Reserva();
        reserva.usuario = new Usuario();
        reserva.usuario.email = "test@test.com";
        reserva.horaInicio = LocalTime.of(10, 0);
        reserva.duracionMinutos = 60;

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva resultado = reservaService.crearReserva(reserva);

        assertNotNull(resultado);
        assertEquals(LocalTime.of(11, 0), resultado.horaFin);
        verify(reservaRepository, times(1)).save(reserva);
        verify(emailService, times(1)).enviarConfirmacion(any());
    }
}