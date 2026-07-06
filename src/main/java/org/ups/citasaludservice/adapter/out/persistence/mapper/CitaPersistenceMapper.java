package org.ups.citasaludservice.adapter.out.persistence.mapper;

import org.ups.citasaludservice.adapter.out.persistence.entity.CitaEntity;
import org.ups.citasaludservice.domain.model.Cita;

public class CitaPersistenceMapper {

    public CitaEntity toEntity(Cita cita) {
        return new CitaEntity(
                cita.id(),
                cita.pacienteId(),
                cita.medicoId(),
                cita.franjaHorariaId(),
                cita.estado(),
                cita.fechaCreacion()
        );
    }

    public Cita toDomain(CitaEntity entity) {
        return new Cita(
                entity.getId(),
                entity.getPacienteId(),
                entity.getMedicoId(),
                entity.getFranjaHorariaId(),
                entity.getEstado(),
                entity.getFechaCreacion()
        );
    }
}
