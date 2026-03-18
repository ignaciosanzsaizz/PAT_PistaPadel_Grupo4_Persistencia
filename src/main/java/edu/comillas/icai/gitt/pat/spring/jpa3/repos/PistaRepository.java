package edu.comillas.icai.gitt.pat.spring.jpa3.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {
    Optional<Pista> findByNombre(String nombre);
}