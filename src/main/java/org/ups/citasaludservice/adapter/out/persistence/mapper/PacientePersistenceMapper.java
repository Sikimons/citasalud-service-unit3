package org.ups.citasaludservice.adapter.out.persistence.mapper;

import org.ups.citasaludservice.adapter.out.persistence.entity.PacienteEntity;
import org.ups.citasaludservice.domain.model.Paciente;

public class PacientePersistenceMapper {

    public PacienteEntity toEntity(Paciente paciente) {
        return new PacienteEntity(paciente.id(), paciente.nombreCompleto(), paciente.numeroWhatsApp());
    }

    public Paciente toDomain(PacienteEntity entity) {
        return new Paciente(entity.getId(), entity.getNombreCompleto(), entity.getNumeroWhatsApp());
    }
}
