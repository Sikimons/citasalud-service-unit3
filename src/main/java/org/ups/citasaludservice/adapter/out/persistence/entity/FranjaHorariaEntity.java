package org.ups.citasaludservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.ups.citasaludservice.domain.model.EstadoFranja;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
    name = "franjas_horarias",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_medico_fecha_hora_inicio",
        columnNames = {"medico_id", "fecha", "hora_inicio"}
    )
)
public class FranjaHorariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "medico_id", nullable = false)
    private UUID medicoId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFranja estado;

    protected FranjaHorariaEntity() {}

    public FranjaHorariaEntity(UUID id, UUID medicoId, LocalDate fecha,
                                LocalTime horaInicio, LocalTime horaFin, EstadoFranja estado) {
        this.id = id;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public UUID getId() { return id; }
    public UUID getMedicoId() { return medicoId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoFranja getEstado() { return estado; }

    public void setEstado(EstadoFranja estado) { this.estado = estado; }
}
