package edu.comillas.icai.gitt.pat.spring.jpa3.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}