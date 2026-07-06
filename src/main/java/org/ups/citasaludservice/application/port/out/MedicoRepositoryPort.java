package org.ups.citasaludservice.application.port.out;

import org.ups.citasaludservice.domain.model.Medico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicoRepositoryPort {

    List<Medico> buscarTodos();

    List<Medico> buscarPorEspecialidad(String especialidad);

    Optional<Medico> buscarPorId(UUID id);
}
