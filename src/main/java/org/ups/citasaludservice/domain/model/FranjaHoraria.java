package org.ups.citasaludservice.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public record FranjaHoraria(
        UUID id,
        UUID medicoId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoFranja estado) {

    public FranjaHoraria {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(medicoId, "medicoId is required");
        Objects.requireNonNull(fecha, "fecha is required");
        Objects.requireNonNull(horaInicio, "horaInicio is required");
        Objects.requireNonNull(horaFin, "horaFin is required");
        Objects.requireNonNull(estado, "estado is required");
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("horaFin debe ser posterior a horaInicio");
        }
    }

    public boolean estaDisponible() {
        return estado == EstadoFranja.DISPONIBLE;
    }

    public FranjaHoraria marcarOcupada() {
        return new FranjaHoraria(id, medicoId, fecha, horaInicio, horaFin, EstadoFranja.OCUPADA);
    }
}
