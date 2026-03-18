package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Rol;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.RolRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PistaRepository pistaRepository;

    @Autowired
    private RolRepository rolRepository;

    @Test
    @DisplayName("Debe buscar reservas por fechaReserva")
    void buscarReservasPorFechaReserva() {
        Rol rol = new Rol();
        rol.nombreRol = "USER";
        rol.descripcion = "Usuario normal";
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.nombre = "Rodrigo";
        usuario.apellidos = "Martin";
        usuario.email = "rodrigo@test.com";
        usuario.password = "1234";
        usuario.telefono = "600000000";
        usuario.rol = rol;
        usuario.fechaRegistro = new Date();
        usuario.activo = true;
        usuario = usuarioRepository.save(usuario);

        Pista pista = new Pista();
        pista.nombre = "Pista 1";
        pista.ubicacion = "Madrid";
        pista.precioHora = 20.0;
        pista.activa = true;
        pista.fechaAlta = new Date();
        pista = pistaRepository.save(pista);

        Reserva reserva = new Reserva();
        reserva.usuario = usuario;
        reserva.pista = pista;
        reserva.fechaReserva = LocalDate.of(2026, 1, 20);
        reserva.horaInicio = LocalTime.of(10, 0);
        reserva.duracionMinutos = 90;
        reserva.calcularHoraFin();
        reserva.estado = EstadoReserva.ACTIVA;
        reserva.fechaCreacion = LocalDateTime.now();
        reservaRepository.save(reserva);

        List<Reserva> reservas = reservaRepository.findByFechaReserva(LocalDate.of(2026, 1, 20));

        assertNotNull(reservas);
        assertEquals(1, reservas.size());
    }

    @Test
    @DisplayName("Debe buscar reservas por usuario")
    void buscarReservasPorUsuario() {
        Rol rol = new Rol();
        rol.nombreRol = "ADMIN";
        rol.descripcion = "Administrador";
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.nombre = "Ana";
        usuario.apellidos = "Lopez";
        usuario.email = "ana@test.com";
        usuario.password = "abcd";
        usuario.telefono = "611111111";
        usuario.rol = rol;
        usuario.fechaRegistro = new Date();
        usuario.activo = true;
        usuario = usuarioRepository.save(usuario);

        Pista pista = new Pista();
        pista.nombre = "Pista 2";
        pista.ubicacion = "Sevilla";
        pista.precioHora = 18.0;
        pista.activa = true;
        pista.fechaAlta = new Date();
        pista = pistaRepository.save(pista);

        Reserva reserva = new Reserva();
        reserva.usuario = usuario;
        reserva.pista = pista;
        reserva.fechaReserva = LocalDate.now();
        reserva.horaInicio = LocalTime.of(12, 0);
        reserva.duracionMinutos = 60;
        reserva.calcularHoraFin();
        reserva.estado = EstadoReserva.ACTIVA;
        reserva.fechaCreacion = LocalDateTime.now();
        reservaRepository.save(reserva);

        List<Reserva> reservas = reservaRepository.findByUsuario(usuario);

        assertEquals(1, reservas.size());
        assertEquals(usuario.email, reservas.get(0).usuario.email);
    }
}