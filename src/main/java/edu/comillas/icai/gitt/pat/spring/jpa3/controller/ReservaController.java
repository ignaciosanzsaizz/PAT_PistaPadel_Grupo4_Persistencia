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

    // Obtiene el usuario autenticado a partir del email guardado en el contexto de seguridad
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario autenticado no encontrado"
                ));
    }

    // Crear una nueva reserva para el usuario autenticado
    @PostMapping("/reservations")
    public Reserva crear(@RequestBody Reserva reserva) {
        Usuario usuario = obtenerUsuarioActual();
        reserva.usuario = usuario;
        return reservaService.crearReserva(reserva);
    }

    // Listar reservas:
    // - ADMIN ve todas
    // - USER ve solo las suyas
    @GetMapping("/reservations")
    public List<Reserva> listar() {
        Usuario usuario = obtenerUsuarioActual();

        // Verificar si es ADMIN comparando por el rol
        if (esAdmin(usuario)) {
            return reservaService.listarTodas();
        }

        return reservaService.obtenerReservasPorUsuario(usuario.id);
    }

    // Obtener una reserva por id si el usuario tiene permiso
    @GetMapping("/reservations/{id}")
    public Reserva obtenerPorId(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        // Si no es admin, solo puede ver sus propias reservas
        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
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
        Reserva reserva = reservaService.obtenerPorId(id);

        // Validar permisos: solo admin o dueño puede modificar
        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        // Actualizar solo los campos permitidos
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

    // Cancelar una reserva
    @DeleteMapping("/reservations/{id}")
    public void cancelar(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Reserva reserva = reservaService.obtenerPorId(id);

        // Validar permisos: solo admin o dueño puede cancelar
        if (!esAdmin(usuario) && !reserva.usuario.id.equals(usuario.id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado"
            );
        }

        reservaService.cancelarReserva(id);
    }

    // Consultar disponibilidad de una pista concreta en una fecha
    @GetMapping("/courts/{id}/availability")
    public List<String> disponibilidad(@PathVariable Long id,
                                       @RequestParam LocalDate date) {
        // Validar que la pista existe
        pistaService.buscarPista(id);
        return disponibilidadService.calcularDisponibilidad(id, date);
    }

    // Endpoint de administración con filtros opcionales
    @GetMapping("/admin/reservations")
    public List<Reserva> listarAdmin(@RequestParam(required = false) LocalDate date,
                                     @RequestParam(required = false) Long courtId,
                                     @RequestParam(required = false) Long userId) {
        Usuario usuario = obtenerUsuarioActual();

        // Solo un administrador puede usar este endpoint
        if (!esAdmin(usuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo ADMIN"
            );
        }

        List<Reserva> reservas = reservaService.listarTodas();

        // Aplicamos filtros solo si vienen informados
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

    // Consultar la disponibilidad global de todas las pistas en una fecha
    @GetMapping("/availability")
    public Map<Long, List<String>> disponibilidadGlobal(@RequestParam LocalDate date) {
        Map<Long, List<String>> resultado = new HashMap<>();

        // Recorremos todas las pistas y calculamos su disponibilidad
        pistaService.listarPistas().forEach(pista ->
                resultado.put(
                        pista.idPista,
                        disponibilidadService.calcularDisponibilidad(pista.idPista, date)
                )
        );

        return resultado;
    }

    // Método auxiliar para verificar si un usuario es ADMIN
    private boolean esAdmin(Usuario usuario) {
        return usuario.rol != null && "ADMIN".equalsIgnoreCase(usuario.rol.nombreRol);
    }
}
