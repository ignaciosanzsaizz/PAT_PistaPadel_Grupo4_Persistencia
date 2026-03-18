package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.entity;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

class ReservaTest {

    @Test
    void calcularHoraFin() {
        Reserva reserva = new Reserva();
        reserva.horaInicio = LocalTime.of(10, 0);
        reserva.duracionMinutos = 90;

        reserva.calcularHoraFin();
        assertEquals(LocalTime.of(11, 30), reserva.horaFin, "La hora de fin debería ser las 11:30");
    }

    @Test
    void inicializacion_valoresPorDefecto() {

        Usuario usuario = new Usuario();
        usuario.activo = true;

        assertTrue(usuario.activo, "El usuario debería estar activo");
    }
}