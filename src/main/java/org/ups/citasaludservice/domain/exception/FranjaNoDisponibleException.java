package org.ups.citasaludservice.domain.exception;

import java.util.UUID;

public class FranjaNoDisponibleException extends RuntimeException {

    private final UUID franjaHorariaId;

    public FranjaNoDisponibleException(UUID franjaHorariaId) {
        super("La franja horaria " + franjaHorariaId + " no está disponible");
        this.franjaHorariaId = franjaHorariaId;
    }

    public UUID getFranjaHorariaId() {
        return franjaHorariaId;
    }
}
