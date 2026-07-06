package org.ups.citasaludservice.application.service;

import org.ups.citasaludservice.application.port.in.ConsultarDisponibilidadUseCase;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.application.port.out.MedicoRepositoryPort;
import org.ups.citasaludservice.domain.model.FranjaHoraria;
import org.ups.citasaludservice.domain.model.Medico;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ConsultarDisponibilidadInteractor implements ConsultarDisponibilidadUseCase {

    private final MedicoRepositoryPort medicoRepo;
    private final FranjaHorariaRepositoryPort franjaRepo;

    public ConsultarDisponibilidadInteractor(MedicoRepositoryPort medicoRepo,
                                              FranjaHorariaRepositoryPort franjaRepo) {
        this.medicoRepo = medicoRepo;
        this.franjaRepo = franjaRepo;
    }

    @Override
    public List<Medico> listarMedicos(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return medicoRepo.buscarTodos();
        }
        return medicoRepo.buscarPorEspecialidad(especialidad);
    }

    @Override
    public List<FranjaHoraria> consultarFranjas(UUID medicoId, LocalDate fecha) {
        return franjaRepo.buscarPorMedicoYFecha(medicoId, fecha);
    }

    @Override
    public List<FranjaHoraria> consultarFranjasDisponibles(UUID medicoId, LocalDate fecha) {
        return franjaRepo.buscarDisponiblesPorMedicoYFecha(medicoId, fecha);
    }
}
