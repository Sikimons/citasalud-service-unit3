package org.ups.citasaludservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Paciente(UUID id, String nombreCompleto, String numeroWhatsApp) {

    public Paciente {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(nombreCompleto, "nombreCompleto is required");
        Objects.requireNonNull(numeroWhatsApp, "numeroWhatsApp is required");
    }
}
