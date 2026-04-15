package edu.comillas.icai.gitt.pat.spring.jpa3.service;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PistaRepository pistaRepository;

    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public List<String> calcularDisponibilidad(Long idPista, LocalDate fecha) {

        List<Reserva> reservasPorFecha = reservaRepository.findByFechaReserva(fecha);
        List<Reserva> reservas = new ArrayList<>();
        
        for (Reserva reserva : reservasPorFecha) {
            if (reserva.pista != null && reserva.pista.idPista.equals(idPista)) {
                reservas.add(reserva);
            }
        }

        reservas.sort(Comparator.comparing(r -> r.horaInicio));

        List<String> huecosLibres = new ArrayList<>();
        LocalTime horaActual = HORA_APERTURA;

        for (Reserva reserva : reservas) {

            if (reserva.horaInicio.isAfter(horaActual)) {
                huecosLibres.add(horaActual + " - " + reserva.horaInicio);
            }

            horaActual = reserva.horaFin;
        }

        if (horaActual.isBefore(HORA_CIERRE)) {
            huecosLibres.add(horaActual + " - " + HORA_CIERRE);
        }

        return huecosLibres;
    }

    public Map<Long, List<String>> calcularDisponibilidadGlobal(LocalDate fecha) {
        Map<Long, List<String>> resultado = new HashMap<>();

        List<Pista> pistas = pistaRepository.findAll();

        for (Pista pista : pistas) {
            resultado.put(
                    pista.idPista,
                    calcularDisponibilidad(pista.idPista, fecha)
            );
        }

        return resultado;
    }
}

