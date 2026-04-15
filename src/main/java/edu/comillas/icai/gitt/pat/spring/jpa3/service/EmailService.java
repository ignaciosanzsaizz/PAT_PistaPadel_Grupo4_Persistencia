package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        log.info("ENVIANDO EMAIL A: {}", destinatario);
        log.info("Asunto: {}", asunto);
        log.info("Cuerpo: {}", mensaje);
    }

    public void enviarConfirmacion(Reserva reserva) {
        log.info("ENVIANDO EMAIL A: {}", reserva.usuario.email);
        log.info("Cuerpo: Estimado {}, su reserva para la pista {} el día {} ha sido confirmada.",
                reserva.usuario.nombre, reserva.pista.nombre, reserva.fechaReserva);
    }
}
