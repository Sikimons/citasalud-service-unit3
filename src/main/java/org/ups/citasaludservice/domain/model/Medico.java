package org.ups.citasaludservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Medico(UUID id, String nombreCompleto, String especialidad) {

    public Medico {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(nombreCompleto, "nombreCompleto is required");
        Objects.requireNonNull(especialidad, "especialidad is required");
    }
}
