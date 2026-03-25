package edu.comillas.icai.gitt.pat.spring.jpa3;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.EmailService;
import edu.comillas.icai.gitt.pat.spring.jpa3.service.UsuarioService;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TareasProgramadas {

    private static final Logger log =
            LoggerFactory.getLogger(TareasProgramadas.class);

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public TareasProgramadas(
            ReservaRepository reservaRepository,
            UsuarioService usuarioService,
            EmailService emailService) {

        this.reservaRepository = reservaRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    // ============================================
    // 1. RECORDATORIO DIARIO (02:00 AM)
    // ============================================
    @Scheduled(cron = "0 0 2 * * *")
    public void enviarRecordatorioReservas() {

        log.info("Ejecutando tarea: recordatorio diario");

        LocalDate hoy = LocalDate.now();

        List<Reserva> reservas = reservaRepository.findByFechaReserva(hoy);

        for (Reserva r : reservas) {
            try {
                String mensaje = "Hola,\n\n" +
                        "Tienes una reserva hoy.\n" +
                        "Pista: " + r.pista.nombre + "\n" +
                        "Hora: " + r.horaInicio;

                emailService.enviarCorreo(
                        r.usuario.email,
                        "Recordatorio de reserva",
                        mensaje
                );

                log.debug("Correo enviado a {}", r.usuario.email);

            } catch (Exception e) {
                log.error("Error enviando recordatorio", e);
            }
        }
    }

    // ============================================
    // 2. EMAIL MENSUAL (día 1 a las 02:00)
    // ============================================
    @Scheduled(cron = "0 0 2 1 * *")
    public void enviarDisponibilidadMensual() {

        log.info("Ejecutando tarea: envío mensual");

        List<Usuario> usuarios = usuarioService.listarTodos();

        for (Usuario u : usuarios) {
            try {
                String mensaje = "Hola " + u.nombre + ",\n\n" +
                        "Consulta la disponibilidad de pistas y horarios en la aplicación.";

                emailService.enviarCorreo(
                        u.email,
                        "Disponibilidad mensual",
                        mensaje
                );

                log.debug("Correo enviado a {}", u.email);

            } catch (Exception e) {
                log.error("Error enviando correo mensual", e);
            }
        }
    }

    // ============================================
    // 3. TAREA DE MANTENIMIENTO (cada hora)
    // ============================================
    @Scheduled(fixedRate = 3600000)
    public void revisarReservasCaducadas() {

        log.debug("Ejecutando tarea programada: revisión de reservas...");

        // TODO: cancelar reservas no pagadas / expiradas

        log.info("Tarea de mantenimiento completada.");
    }
}
