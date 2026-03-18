package edu.comillas.icai.gitt.pat.spring.jpa3.entity;

import jakarta.persistence.*;

@Entity
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(nullable = false, unique = true)
    public String nombreRol;

    @Column
    public String descripcion;
}
