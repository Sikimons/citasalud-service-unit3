package org.ups.citasaludservice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record Notificacion(
        UUID id,
        TipoNotificacion tipo,
        String destinatario,
        String contenido,
        EstadoEnvio estadoEnvio,
        LocalDateTime timestamp) {

    public Notificacion {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(tipo, "tipo is required");
        Objects.requireNonNull(destinatario, "destinatario is required");
        Objects.requireNonNull(contenido, "contenido is required");
        Objects.requireNonNull(estadoEnvio, "estadoEnvio is required");
        Objects.requireNonNull(timestamp, "timestamp is required");
    }
}
