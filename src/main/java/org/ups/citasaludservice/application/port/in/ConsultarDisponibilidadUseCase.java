package org.ups.citasaludservice.application.port.in;

import org.ups.citasaludservice.domain.model.FranjaHoraria;
import org.ups.citasaludservice.domain.model.Medico;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConsultarDisponibilidadUseCase {

    List<Medico> listarMedicos(String especialidad);

    List<FranjaHoraria> consultarFranjas(UUID medicoId, LocalDate fecha);

    List<FranjaHoraria> consultarFranjasDisponibles(UUID medicoId, LocalDate fecha);
}
