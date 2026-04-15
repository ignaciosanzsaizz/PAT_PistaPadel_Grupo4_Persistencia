package edu.comillas.icai.gitt.pat.spring.jpa3.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Reserva;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByFechaReserva(LocalDate fechaReserva);

    default List<Reserva> findByFecha(LocalDate fecha) {
        return findByFechaReserva(fecha);
    }

    List<Reserva> findByUsuario(Usuario usuario);
    List<Reserva> findByUsuario_Id(Long id);
    List<Reserva> findByPista(Pista pista);
}