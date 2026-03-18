package edu.comillas.icai.gitt.pat.spring.jpa3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TareasProgramadas {

    private static final Logger log =
            LoggerFactory.getLogger(TareasProgramadas.class);

    // ============================================
    // RECORDATORIO DIARIO
    // ============================================
    @Scheduled(cron = "0 0 2 * * *")
    public void recordatorioDiario() {

        log.info("Ejecutando recordatorio diario (SIMULACIÓN)");

        // TODO: llamar a ReservaService cuando esté disponible
        log.debug("Aquí se enviarán emails a usuarios con reserva hoy");
    }

    // ============================================
    // EMAIL MENSUAL
    // ============================================
    @Scheduled(cron = "0 0 2 1 * *")
    public void envioMensual() {

        log.info("Ejecutando envío mensual (SIMULACIÓN)");

        // TODO: llamar a UsuarioService cuando esté disponible
        log.debug("Aquí se enviará disponibilidad a todos los usuarios");
    }
}