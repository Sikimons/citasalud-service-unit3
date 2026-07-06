package org.ups.citasaludservice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Cita(
        UUID id,
        UUID pacienteId,
        UUID medicoId,
        UUID franjaHorariaId,
        EstadoCita estado,
        LocalDateTime fechaCreacion) {

    public Cita {
        Objects.requireNonNull(pacienteId, "pacienteId is required");
        Objects.requireNonNull(medicoId, "medicoId is required");
        Objects.requireNonNull(franjaHorariaId, "franjaHorariaId is required");
        Objects.requireNonNull(estado, "estado is required");
        Objects.requireNonNull(fechaCreacion, "fechaCreacion is required");
    }
}
