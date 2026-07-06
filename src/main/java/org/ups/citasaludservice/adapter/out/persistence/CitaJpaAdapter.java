package org.ups.citasaludservice.adapter.out.persistence;

import org.ups.citasaludservice.adapter.out.persistence.mapper.CitaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.CitaJpaRepository;
import org.ups.citasaludservice.application.port.out.CitaRepositoryPort;
import org.ups.citasaludservice.domain.model.Cita;

import java.util.Optional;
import java.util.UUID;

public class CitaJpaAdapter implements CitaRepositoryPort {

    private final CitaJpaRepository repo;
    private final CitaPersistenceMapper mapper;

    public CitaJpaAdapter(CitaJpaRepository repo, CitaPersistenceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Cita guardar(Cita cita) {
        return mapper.toDomain(repo.save(mapper.toEntity(cita)));
    }

    @Override
    public Optional<Cita> buscarPorId(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }
}
