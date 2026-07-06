package org.ups.citasaludservice.application.port.out;

import org.ups.citasaludservice.domain.model.Cita;

import java.util.Optional;
import java.util.UUID;

public interface CitaRepositoryPort {

    Cita guardar(Cita cita);

    Optional<Cita> buscarPorId(UUID id);
}
