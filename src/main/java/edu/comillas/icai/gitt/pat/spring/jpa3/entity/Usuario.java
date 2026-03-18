package edu.comillas.icai.gitt.pat.spring.jpa3.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String nombre;

    @Column(nullable = false)
    public String apellidos;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String telefono;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    public Rol rol;

    @Column(nullable = false)
    public Date fechaRegistro;

    @Column(nullable = false)
    public Boolean activo;

}
