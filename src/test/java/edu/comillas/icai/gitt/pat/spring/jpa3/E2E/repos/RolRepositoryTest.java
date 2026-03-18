package edu.comillas.icai.gitt.pat.spring.jpa3.E2E.repos;

import edu.comillas.icai.gitt.pat.spring.jpa3.entity.Rol;
import edu.comillas.icai.gitt.pat.spring.jpa3.repos.RolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    @Test
    @DisplayName("Debe buscar rol por nombreRol")
    void buscarRolPorNombreRol() {
        Rol rol = new Rol();
        rol.nombreRol = "ADMIN";
        rol.descripcion = "Administrador";
        rolRepository.save(rol);

        Optional<Rol> resultado = rolRepository.findByNombreRol("ADMIN");

        assertTrue(resultado.isPresent());
        assertEquals("Administrador", resultado.get().descripcion);
    }
}