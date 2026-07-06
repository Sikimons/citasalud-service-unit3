package org.ups.citasaludservice.application.port.out;

import org.ups.citasaludservice.domain.model.FranjaHoraria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FranjaHorariaRepositoryPort {

    Optional<FranjaHoraria> buscarPorIdConBloqueo(UUID id);

    Optional<FranjaHoraria> buscarPorId(UUID id);

    FranjaHoraria guardar(FranjaHoraria franja);

    List<FranjaHoraria> buscarPorMedicoYFecha(UUID medicoId, LocalDate fecha);

    List<FranjaHoraria> buscarDisponiblesPorMedicoYFecha(UUID medicoId, LocalDate fecha);
}
