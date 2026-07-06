package org.ups.citasaludservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.ups.citasaludservice.domain.model.EstadoCita;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "citas")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "medico_id", nullable = false)
    private UUID medicoId;

    @Column(name = "franja_horaria_id", nullable = false)
    private UUID franjaHorariaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    protected CitaEntity() {}

    public CitaEntity(UUID id, UUID pacienteId, UUID medicoId, UUID franjaHorariaId,
                      EstadoCita estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.franjaHorariaId = franjaHorariaId;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public UUID getId() { return id; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getMedicoId() { return medicoId; }
    public UUID getFranjaHorariaId() { return franjaHorariaId; }
    public EstadoCita getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setEstado(EstadoCita estado) { this.estado = estado; }
}
