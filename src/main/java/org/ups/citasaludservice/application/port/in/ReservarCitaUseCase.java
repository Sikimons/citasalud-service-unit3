package org.ups.citasaludservice.application.port.in;

import org.ups.citasaludservice.domain.model.Cita;

import java.util.UUID;

public interface ReservarCitaUseCase {

    Cita reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId);
}
