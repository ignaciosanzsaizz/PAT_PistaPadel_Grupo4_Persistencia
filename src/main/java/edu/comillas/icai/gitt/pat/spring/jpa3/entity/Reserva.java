package edu.comillas.icai.gitt.pat.spring.jpa3.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idReserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    public Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_pista", nullable = false)
    public Pista pista;

    @Column(nullable = false)
    public LocalDate fechaReserva;

    @Column(nullable = false)
    public LocalTime horaInicio;

    @Column(nullable = false)
    public Integer duracionMinutos;

    @Column(nullable = false)
    public LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public EstadoReserva estado;

    @Column(nullable = false)
    public LocalDateTime fechaCreacion;

    public void calcularHoraFin() {
        if (this.horaInicio != null && this.duracionMinutos != null) {
            this.horaFin = this.horaInicio.plusMinutes(this.duracionMinutos);
        }
    }
}
