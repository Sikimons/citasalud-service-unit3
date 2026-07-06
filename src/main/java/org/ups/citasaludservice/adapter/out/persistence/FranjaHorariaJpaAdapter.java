package org.ups.citasaludservice.adapter.out.persistence;

import org.ups.citasaludservice.adapter.out.persistence.entity.FranjaHorariaEntity;
import org.ups.citasaludservice.adapter.out.persistence.mapper.FranjaHorariaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.FranjaHorariaJpaRepository;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.domain.model.EstadoFranja;
import org.ups.citasaludservice.domain.model.FranjaHoraria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FranjaHorariaJpaAdapter implements FranjaHorariaRepositoryPort {

    private final FranjaHorariaJpaRepository repo;
    private final FranjaHorariaPersistenceMapper mapper;

    public FranjaHorariaJpaAdapter(FranjaHorariaJpaRepository repo,
                                    FranjaHorariaPersistenceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Optional<FranjaHoraria> buscarPorIdConBloqueo(UUID id) {
        return repo.findByIdWithLock(id).map(mapper::toDomain);
    }

    @Override
    public Optional<FranjaHoraria> buscarPorId(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public FranjaHoraria guardar(FranjaHoraria franja) {
        FranjaHorariaEntity entity = repo.findById(franja.id())
                .orElse(mapper.toEntity(franja));
        entity.setEstado(franja.estado());
        return mapper.toDomain(repo.save(entity));
    }

    @Override
    public List<FranjaHoraria> buscarPorMedicoYFecha(UUID medicoId, LocalDate fecha) {
        return repo.findByMedicoIdAndFecha(medicoId, fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FranjaHoraria> buscarDisponiblesPorMedicoYFecha(UUID medicoId, LocalDate fecha) {
        return repo.findByMedicoIdAndFechaAndEstado(medicoId, fecha, EstadoFranja.DISPONIBLE).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
