package org.ups.citasaludservice.adapter.out.persistence;

import org.ups.citasaludservice.adapter.out.persistence.mapper.MedicoPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.MedicoJpaRepository;
import org.ups.citasaludservice.application.port.out.MedicoRepositoryPort;
import org.ups.citasaludservice.domain.model.Medico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MedicoJpaAdapter implements MedicoRepositoryPort {

    private final MedicoJpaRepository repo;
    private final MedicoPersistenceMapper mapper;

    public MedicoJpaAdapter(MedicoJpaRepository repo, MedicoPersistenceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<Medico> buscarTodos() {
        return repo.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Medico> buscarPorEspecialidad(String especialidad) {
        return repo.findByEspecialidad(especialidad).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Medico> buscarPorId(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }
}
