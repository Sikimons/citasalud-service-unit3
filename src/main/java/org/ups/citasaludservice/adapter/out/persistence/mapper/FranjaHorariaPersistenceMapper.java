package org.ups.citasaludservice.adapter.out.persistence.mapper;

import org.ups.citasaludservice.adapter.out.persistence.entity.FranjaHorariaEntity;
import org.ups.citasaludservice.domain.model.FranjaHoraria;

public class FranjaHorariaPersistenceMapper {

    public FranjaHorariaEntity toEntity(FranjaHoraria franja) {
        return new FranjaHorariaEntity(
                franja.id(),
                franja.medicoId(),
                franja.fecha(),
                franja.horaInicio(),
                franja.horaFin(),
                franja.estado()
        );
    }

    public FranjaHoraria toDomain(FranjaHorariaEntity entity) {
        return new FranjaHoraria(
                entity.getId(),
                entity.getMedicoId(),
                entity.getFecha(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.getEstado()
        );
    }
}
