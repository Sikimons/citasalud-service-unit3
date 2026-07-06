package org.ups.citasaludservice.adapter.in.web.mapper;

import org.ups.citasaludservice.adapter.in.web.api.model.CitaResponse;
import org.ups.citasaludservice.domain.model.Cita;
import org.ups.citasaludservice.domain.model.FranjaHoraria;
import org.ups.citasaludservice.domain.model.Medico;

public class CitaWebMapper {

    private final MedicoWebMapper medicoMapper;
    private final FranjaHorariaWebMapper franjaMapper;

    public CitaWebMapper(MedicoWebMapper medicoMapper, FranjaHorariaWebMapper franjaMapper) {
        this.medicoMapper = medicoMapper;
        this.franjaMapper = franjaMapper;
    }

    public CitaResponse toDto(Cita cita, Medico medico, FranjaHoraria franja) {
        CitaResponse dto = new CitaResponse();
        dto.setId(cita.id());
        dto.setMedico(medicoMapper.toDto(medico));
        dto.setFranjaHoraria(franjaMapper.toDto(franja));
        dto.setEstado(CitaResponse.EstadoEnum.valueOf(cita.estado().name()));
        dto.setFechaCreacion(cita.fechaCreacion().atOffset(java.time.ZoneOffset.UTC));
        return dto;
    }
}
