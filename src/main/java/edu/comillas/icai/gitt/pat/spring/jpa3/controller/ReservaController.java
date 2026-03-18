package edu.comillas.icai.gitt.pat.spring.jpa3.controller;


import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.UsuarioRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.DisponibilidadService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pistaPadel")
public class ReservaController {
    // Servicio principal para la lógica de reservas
    private final ReservaService reservaService;

    // Servicio para calcular disponibilidad
    private final DisponibilidadService disponibilidadService;

    // Repositorio de usuarios para obtener el usuario autenticado
    private final UsuarioRepository usuarioRepository;

    // Repositorio de pistas para consultar disponibilidad global
    private final PistaRepository pistaRepository;

    public ReservaController(ReservaService reservaService,
                             DisponibilidadService disponibilidadService,
                             UsuarioRepository usuarioRepository,
                             PistaRepository pistaRepository) {
        this.reservaService = reservaService;
        this.disponibilidadService= disponibilidadService;
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
    }

    // Obtiene el usuario autenticado a partir del email guardado en el contexto de seguridad
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario autenticado no encontrado"
                ));
    }

    // Crear una nueva reserva para el usuario autenticado
    @PostMapping("/reservations")
    public Reserva crear(@RequestBody Reserva reserva) {
        Usuario usuario = obtenerUsuarioActual();

        return reservaService.crearReserva(
                usuario.getIdUsuario(),
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getDuracionMinutos()
        );
    }

    // Listar reservas:
    // - ADMIN ve todas
    // - USER ve solo las suyas
    @GetMapping("/reservations")
    public List<Reserva> listar() {
        Usuario usuario = obtenerUsuarioActual();

        if (usuario.getRol() == Rol.ADMIN) {
            return reservaService.listarTodas();
        }

        return reservaService.listarReservasUsuario(usuario.getIdUsuario());
    }

    // Obtener una reserva por id si el usuario tiene permiso
    @GetMapping("/reservations/{id}")
    public Reserva obtenerPorId(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        // Si no es admin, solo puede ver sus propias reservas
        if (usuario.getRol() != Rol.ADMIN &&
                !reserva.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        return reserva;
    }

    // Modificar una reserva existente
    @PatchMapping("/reservations/{id}")
    public Reserva modificar(@PathVariable Long id, @RequestBody Reserva datos) {
        Usuario usuario = obtenerUsuarioActual();

        return reservaService.modificarReserva(
                id,
                usuario.getIdUsuario(),
                usuario.getRol() == Rol.ADMIN,
                datos.getFechaReserva(),
                datos.getHoraInicio(),
                datos.getDuracionMinutos()
        );
    }

    // Cancelar una reserva
    @DeleteMapping("/reservations/{id}")
    public void cancelar(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();

        reservaService.cancelarReserva(
                id,
                usuario.getIdUsuario(),
                usuario.getRol() == Rol.ADMIN
        );
    }

    // Consultar disponibilidad de una pista concreta en una fecha
    @GetMapping("/courts/{id}/availability")
    public List<String> disponibilidad(@PathVariable Long id,
                                       @RequestParam LocalDate date) {
        return disponibilidadService.calcularDisponibilidad(id, date);
    }

    // Endpoint de administración con filtros opcionales
    @GetMapping("/admin/reservations")
    public List<Reserva> listarAdmin(@RequestParam(required = false) LocalDate date,
                                     @RequestParam(required = false) Long courtId,
                                     @RequestParam(required = false) Long userId) {
        Usuario usuario = obtenerUsuarioActual();

        // Solo un administrador puede usar este endpoint
        if (usuario.getRol() != Rol.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo ADMIN"
            );
        }

        List<Reserva> reservas = reservaService.listarTodas();

        // Aplicamos filtros solo si vienen informados
        if (date != null) {
            reservas.removeIf(r -> !date.equals(r.getFechaReserva()));
        }
        if (courtId != null) {
            reservas.removeIf(r -> !courtId.equals(r.getIdPista()));
        }
        if (userId != null) {
            reservas.removeIf(r -> !userId.equals(r.getIdUsuario()));
        }

        return reservas;
    }

    // Consultar la disponibilidad global de todas las pistas en una fecha
    @GetMapping("/availability")
    public Map<Long, List<String>> disponibilidadGlobal(@RequestParam LocalDate date) {
        Map<Long, List<String>> resultado = new HashMap<>();

        // Recorremos todas las pistas y calculamos su disponibilidad
        pistaRepository.listar().forEach(pista ->
                resultado.put(
                        pista.getIdPista(),
                        disponibilidadService.calcularDisponibilidad(pista.getIdPista(), date)
                )
        );

        return resultado;
    }

}
