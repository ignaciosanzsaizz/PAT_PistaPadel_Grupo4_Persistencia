package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);
    private final ReservaRepository reservaRepository;
    private final EmailService emailService;

    public ReservaService(ReservaRepository reservaRepository, EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.emailService = emailService;
    }

    public Reserva crearReserva(Reserva reserva) {
        log.debug("Iniciando proceso de reserva para usuario: {}", reserva.usuario.email);

        // Lógica de negocio de la entidad
        reserva.calcularHoraFin();
        reserva.estado = EstadoReserva.ACTIVA;
        reserva.fechaCreacion = LocalDateTime.now();

        try {
            Reserva guardada = reservaRepository.save(reserva);
            log.info("Reserva ID {} creada con éxito", guardada.idReserva);

            // Notificar por email
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

}