package org.ups.citasaludservice.adapter.out.persistence;

import org.ups.citasaludservice.adapter.out.persistence.mapper.PacientePersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.PacienteJpaRepository;
import org.ups.citasaludservice.application.port.out.PacienteRepositoryPort;
import org.ups.citasaludservice.domain.model.Paciente;

import java.util.Optional;
import java.util.UUID;

public class PacienteJpaAdapter implements PacienteRepositoryPort {

    private final PacienteJpaRepository repo;
    private final PacientePersistenceMapper mapper;

    public PacienteJpaAdapter(PacienteJpaRepository repo, PacientePersistenceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Paciente> buscarPorId(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }
}
