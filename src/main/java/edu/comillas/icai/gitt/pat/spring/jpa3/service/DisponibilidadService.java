package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import org.springframework.stereotype.Service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.ReservaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisponibilidadService {
    // Repositorio para consultar las reservas existentes
    private final ReservaRepository reservaRepository;

    // Repositorio para obtener todas las pistas en disponibilidad global
    private final PistaRepository pistaRepository;

    // Horario fijo del club
    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public DisponibilidadService(ReservaRepository reservaRepository,
                                 PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
    }

    // Calcula los huecos libres de una pista en una fecha concreta
    public List<String> calcularDisponibilidad(Long idPista, LocalDate fecha) {

        // Obtenemos las reservas de esa pista y esa fecha
        List<Reserva> reservas = reservaRepository.listarPorPistaYFecha(idPista, fecha);

        // Ordenamos por hora de inicio para recorrer el día correctamente
        reservas.sort(Comparator.comparing(Reserva::getHoraInicio));

        List<String> huecosLibres = new ArrayList<>();
        LocalTime horaActual = HORA_APERTURA;

        for (Reserva reserva : reservas) {

            // Si la siguiente reserva empieza después de la hora actual,
            // entonces hay un hueco libre
            if (reserva.getHoraInicio().isAfter(horaActual)) {
                huecosLibres.add(horaActual + " - " + reserva.getHoraInicio());
            }

            // Avanzamos la hora actual al final de la reserva
            // Si tu entidad Reserva NO tiene getHoraFin(), usa plusMinutes(...)
            horaActual = reserva.getHoraFin();
        }

        // Si todavía queda tiempo hasta el cierre, añadimos el último hueco
        if (horaActual.isBefore(HORA_CIERRE)) {
            huecosLibres.add(horaActual + " - " + HORA_CIERRE);
        }

        return huecosLibres;
    }

    // Calcula la disponibilidad de todas las pistas en una fecha
    public Map<Long, List<String>> calcularDisponibilidadGlobal(LocalDate fecha) {
        Map<Long, List<String>> resultado = new HashMap<>();

        List<Pista> pistas = pistaRepository.findAll();

        for (Pista pista : pistas) {
            resultado.put(
                    pista.getIdPista(),
                    calcularDisponibilidad(pista.getIdPista(), fecha)
            );
        }

        return resultado;
    }
}

