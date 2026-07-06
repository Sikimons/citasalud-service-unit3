package org.ups.citasaludservice.adapter.in.web.mapper;

import org.ups.citasaludservice.adapter.in.web.api.model.MedicoResponse;
import org.ups.citasaludservice.domain.model.Medico;

public class MedicoWebMapper {

    public MedicoResponse toDto(Medico medico) {
        return new MedicoResponse()
                .id(medico.id())
                .nombreCompleto(medico.nombreCompleto())
                .especialidad(medico.especialidad());
    }
}
