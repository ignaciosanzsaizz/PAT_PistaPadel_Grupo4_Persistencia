package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private EmailService emailService;

    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        log.debug("Iniciando proceso de reserva para usuario: {}", reserva.usuario.email);

        reserva.calcularHoraFin();
        validarSolapeReserva(reserva, null);
        reserva.estado = EstadoReserva.ACTIVA;
        reserva.fechaCreacion = LocalDateTime.now();

        try {
            Reserva guardada = reservaRepository.save(reserva);
            log.info("Reserva ID {} creada con éxito", guardada.idReserva);

            emailService.enviarConfirmacion(guardada);
            return guardada;
        } catch (Exception e) {
            log.error("Error crítico al guardar la reserva: {}", e.getMessage());
            throw e;
        }
    }

    public List<Reserva> obtenerReservasPorUsuario(Long idUsuario) {
        log.debug("Consultando reservas del usuario ID: {}", idUsuario);
        return reservaRepository.findByUsuario_Id(idUsuario);
    }

    @Transactional
    public Reserva modificarReserva(Reserva cambios) {
        log.debug("Modificando reserva ID: {}", cambios.idReserva);
        cambios.calcularHoraFin();
        validarSolapeReserva(cambios, cambios.idReserva);
        return reservaRepository.save(cambios);
    }

    private void validarSolapeReserva(Reserva candidata, Long idReservaActual) {
        if (candidata.pista == null || candidata.pista.idPista == null || candidata.fechaReserva == null
                || candidata.horaInicio == null || candidata.horaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de reserva incompletos");
        }

        List<Reserva> reservasPista = reservaRepository.findByPista(candidata.pista);

        for (Reserva existente : reservasPista) {
            if (existente == null || existente.fechaReserva == null || existente.horaInicio == null
                    || existente.horaFin == null || existente.estado != EstadoReserva.ACTIVA) {
                continue;
            }

            if (!existente.fechaReserva.equals(candidata.fechaReserva)) {
                continue;
            }

            if (idReservaActual != null && idReservaActual.equals(existente.idReserva)) {
                continue;
            }

            boolean haySolape = seSolapan(
                    candidata.horaInicio,
                    candidata.horaFin,
                    existente.horaInicio,
                    existente.horaFin
            );

            if (haySolape) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot ocupado para esa pista y fecha");
            }
        }
    }

    private boolean seSolapan(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && finA.isAfter(inicioB);
    }

    @Transactional
    public void cancelarReserva(Long id) {
        log.debug("Cancelando reserva ID: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.estado = EstadoReserva.CANCELADA;
        reservaRepository.save(reserva);
        log.info("Reserva ID {} cancelada", id);
    }

    public List<Reserva> listarTodas() {
        log.debug("Listando todas las reservas");
        return reservaRepository.findAll();
    }

    public Reserva obtenerPorId(Long id) {
        log.debug("Obteniendo reserva ID: {}", id);
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }
}