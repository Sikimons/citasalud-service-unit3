package org.ups.citasaludservice.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.ups.citasaludservice.adapter.in.web.api.CitasApi;
import org.ups.citasaludservice.adapter.in.web.api.model.CitaResponse;
import org.ups.citasaludservice.adapter.in.web.api.model.ReservarCitaRequest;
import org.ups.citasaludservice.adapter.in.web.mapper.CitaWebMapper;
import org.ups.citasaludservice.application.port.in.ReservarCitaUseCase;
import org.ups.citasaludservice.application.port.out.CitaRepositoryPort;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.application.port.out.MedicoRepositoryPort;
import org.ups.citasaludservice.domain.model.Cita;
import org.ups.citasaludservice.domain.model.FranjaHoraria;
import org.ups.citasaludservice.domain.model.Medico;

import java.net.URI;
import java.util.UUID;

@RestController
public class CitaController implements CitasApi {

    private final ReservarCitaUseCase reservarCitaUseCase;
    private final CitaRepositoryPort citaRepositoryPort;
    private final MedicoRepositoryPort medicoRepositoryPort;
    private final FranjaHorariaRepositoryPort franjaHorariaRepositoryPort;
    private final CitaWebMapper citaWebMapper;

    public CitaController(ReservarCitaUseCase reservarCitaUseCase,
                          CitaRepositoryPort citaRepositoryPort,
                          MedicoRepositoryPort medicoRepositoryPort,
                          FranjaHorariaRepositoryPort franjaHorariaRepositoryPort,
                          CitaWebMapper citaWebMapper) {
        this.reservarCitaUseCase = reservarCitaUseCase;
        this.citaRepositoryPort = citaRepositoryPort;
        this.medicoRepositoryPort = medicoRepositoryPort;
        this.franjaHorariaRepositoryPort = franjaHorariaRepositoryPort;
        this.citaWebMapper = citaWebMapper;
    }

    @Override
    public ResponseEntity<CitaResponse> reservarCita(ReservarCitaRequest request) {
        // pacienteId extracted from JWT in a real implementation; using placeholder here
        UUID pacienteId = UUID.fromString("b1c2d3e4-0001-0001-0001-000000000001");

        Cita cita = reservarCitaUseCase.reservar(
                pacienteId,
                request.getMedicoId(),
                request.getFranjaHorariaId());

        CitaResponse response = buildCitaResponse(cita);
        return ResponseEntity.created(URI.create("/api/v1/citas/" + cita.id()))
                .body(response);
    }

    @Override
    public ResponseEntity<CitaResponse> obtenerCita(UUID citaId) {
        Cita cita = citaRepositoryPort.buscarPorId(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + citaId));
        CitaResponse response = buildCitaResponse(cita);
        return ResponseEntity.ok(response);
    }

    private CitaResponse buildCitaResponse(Cita cita) {
        Medico medico = medicoRepositoryPort.buscarPorId(cita.medicoId())
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado: " + cita.medicoId()));
        FranjaHoraria franja = franjaHorariaRepositoryPort.buscarPorIdConBloqueo(cita.franjaHorariaId())
                .orElseThrow(() -> new IllegalArgumentException("Franja no encontrada: " + cita.franjaHorariaId()));
        return citaWebMapper.toDto(cita, medico, franja);
    }
}
