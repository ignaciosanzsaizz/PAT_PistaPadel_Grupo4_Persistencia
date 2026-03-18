package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TareasProgramadas {
    private static final Logger log = LoggerFactory.getLogger(TareasProgramadas.class);

    // Se ejecuta cada hora (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    public void revisarReservasCaducadas() {
        log.debug("Ejecutando tarea programada: Revisión de reservas...");
        // Aquí iría la lógica para cancelar reservas que no se pagaron, etc.
        log.info("Tarea de mantenimiento completada.");
    }
}