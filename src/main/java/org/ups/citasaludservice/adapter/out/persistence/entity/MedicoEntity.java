package org.ups.citasaludservice.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "medicos")
public class MedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String especialidad;

    protected MedicoEntity() {}

    public MedicoEntity(UUID id, String nombreCompleto, String especialidad) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }

    public UUID getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEspecialidad() { return especialidad; }
}
