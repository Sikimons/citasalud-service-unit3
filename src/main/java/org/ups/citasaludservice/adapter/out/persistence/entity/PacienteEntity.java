package org.ups.citasaludservice.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "pacientes")
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "numero_whatsapp", nullable = false, unique = true)
    private String numeroWhatsApp;

    protected PacienteEntity() {}

    public PacienteEntity(UUID id, String nombreCompleto, String numeroWhatsApp) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.numeroWhatsApp = numeroWhatsApp;
    }

    public UUID getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getNumeroWhatsApp() { return numeroWhatsApp; }
}
