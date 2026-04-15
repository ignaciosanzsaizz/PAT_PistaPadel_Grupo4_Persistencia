package edu.comillas.icai.gitt.pat.spring.jpa3.controller;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.DisponibilidadService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pistaPadel")
public class ReservaController {
    
    @Autowired
    private ReservaService reservaService;

    @Autowired
    private DisponibilidadService disponibilidadService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PistaService pistaService;

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario autenticado no encontrado"
                ));
    }

    @PostMapping("/reservations")
    public Reserva crear(@RequestBody Reserva reserva) {
        Usuario usuario = obtenerUsuarioActual();
        reserva.usuario = usuario;
        return reservaService.crearReserva(reserva);
    }

    @GetMapping("/reservations")
    public List<Reserva> listar() {
        Usuario usuario = obtenerUsuarioActual();

        if (esAdmin(usuario)) {
            return reservaService.listarTodas();
        }

        return reservaService.obtenerReservasPorUsuario(usuario.id);
    }

    @GetMapping("/reservations/{id}")
    public Reserva obtenerPorId(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        return reserva;
    }

    @PatchMapping("/reservations/{id}")
    public Reserva modificar(@PathVariable Long id, @RequestBody Reserva datos) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        if (datos.fechaReserva != null) {
            reserva.fechaReserva = datos.fechaReserva;
        }
        if (datos.horaInicio != null) {
            reserva.horaInicio = datos.horaInicio;
        }
        if (datos.duracionMinutos != null) {
            reserva.duracionMinutos = datos.duracionMinutos;
        }

        return reservaService.modificarReserva(reserva);
    }

    @DeleteMapping("/reservations/{id}")
    public void cancelar(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        reservaService.cancelarReserva(id);
    }

    @GetMapping("/courts/{id}/availability")
    public List<String> disponibilidad(@PathVariable Long id,
                                       @RequestParam LocalDate date) {
        pistaService.buscarPista(id);
        return disponibilidadService.calcularDisponibilidad(id, date);
    }

    @GetMapping("/admin/reservations")
    public List<Reserva> listarAdmin(@RequestParam(required = false) LocalDate date,
                                     @RequestParam(required = false) Long courtId,
                                     @RequestParam(required = false) Long userId) {
        Usuario usuario = obtenerUsuarioActual();

        if (!esAdmin(usuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo ADMIN"
            );
        }

        List<Reserva> reservas = reservaService.listarTodas();

        if (date != null) {
            reservas.removeIf(r -> !date.equals(r.fechaReserva));
        }
        if (courtId != null) {
            reservas.removeIf(r -> !courtId.equals(r.pista.idPista));
        }
        if (userId != null) {
            reservas.removeIf(r -> !userId.equals(r.usuario.id));
        }

        return reservas;
    }

    @GetMapping("/availability")
    public Map<Long, List<String>> disponibilidadGlobal(@RequestParam LocalDate date) {
        Map<Long, List<String>> resultado = new HashMap<>();

        pistaService.listarPistas().forEach(pista ->
                resultado.put(
                        pista.idPista,
                        disponibilidadService.calcularDisponibilidad(pista.idPista, date)
                )
        );

        return resultado;
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario.rol != null && "ADMIN".equalsIgnoreCase(usuario.rol.nombreRol);
    }
}
