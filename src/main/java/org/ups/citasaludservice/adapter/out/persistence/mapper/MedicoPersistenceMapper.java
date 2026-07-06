package org.ups.citasaludservice.adapter.out.persistence.mapper;

import org.ups.citasaludservice.adapter.out.persistence.entity.MedicoEntity;
import org.ups.citasaludservice.domain.model.Medico;

public class MedicoPersistenceMapper {

    public MedicoEntity toEntity(Medico medico) {
        return new MedicoEntity(medico.id(), medico.nombreCompleto(), medico.especialidad());
    }

    public Medico toDomain(MedicoEntity entity) {
        return new Medico(entity.getId(), entity.getNombreCompleto(), entity.getEspecialidad());
    }
}
