package org.ups.citasaludservice.domain.exception;

public class FechaPasadaException extends RuntimeException {

    public FechaPasadaException() {
        super("No es posible reservar una cita en una fecha u hora pasada");
    }
}
