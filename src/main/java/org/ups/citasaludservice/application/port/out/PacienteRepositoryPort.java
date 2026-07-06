package org.ups.citasaludservice.application.port.out;

import org.ups.citasaludservice.domain.model.Paciente;

import java.util.Optional;
import java.util.UUID;

public interface PacienteRepositoryPort {

    Optional<Paciente> buscarPorId(UUID id);
}
