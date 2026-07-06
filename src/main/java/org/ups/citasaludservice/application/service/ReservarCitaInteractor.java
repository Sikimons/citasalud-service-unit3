package org.ups.citasaludservice.application.service;

import jakarta.transaction.Transactional;
import org.ups.citasaludservice.application.port.in.ReservarCitaUseCase;
import org.ups.citasaludservice.application.port.out.*;
import org.ups.citasaludservice.domain.exception.FechaPasadaException;
import org.ups.citasaludservice.domain.exception.FranjaNoDisponibleException;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservarCitaInteractor implements ReservarCitaUseCase {

    private final FranjaHorariaRepositoryPort franjaRepo;
    private final CitaRepositoryPort citaRepo;
    private final PacienteRepositoryPort pacienteRepo;
    private final NotificacionGatewayPort notificacionGateway;

    public ReservarCitaInteractor(FranjaHorariaRepositoryPort franjaRepo,
                                   CitaRepositoryPort citaRepo,
                                   PacienteRepositoryPort pacienteRepo,
                                   NotificacionGatewayPort notificacionGateway) {
        this.franjaRepo = franjaRepo;
        this.citaRepo = citaRepo;
        this.pacienteRepo = pacienteRepo;
        this.notificacionGateway = notificacionGateway;
    }

    @Override
    @Transactional
    public Cita reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId) {
        FranjaHoraria franja = franjaRepo.buscarPorIdConBloqueo(franjaHorariaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Franja horaria no encontrada: " + franjaHorariaId));

        if (franja.fecha().isBefore(LocalDate.now())) {
            throw new FechaPasadaException();
        }

        if (!franja.estaDisponible()) {
            throw new FranjaNoDisponibleException(franjaHorariaId);
        }

        FranjaHoraria franjaOcupada = franja.marcarOcupada();
        franjaRepo.guardar(franjaOcupada);

        Cita cita = new Cita(null, pacienteId, medicoId, franjaHorariaId,
                EstadoCita.CONFIRMADA, LocalDateTime.now());
        Cita citaGuardada = citaRepo.guardar(cita);

        pacienteRepo.buscarPorId(pacienteId).ifPresent(paciente ->
                notificacionGateway.enviarConfirmacion(citaGuardada, paciente.numeroWhatsApp()));

        return citaGuardada;
    }
}
