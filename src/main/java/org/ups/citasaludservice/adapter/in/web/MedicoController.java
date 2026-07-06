package org.ups.citasaludservice.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.ups.citasaludservice.adapter.in.web.api.MedicosApi;
import org.ups.citasaludservice.adapter.in.web.api.model.FranjaHorariaResponse;
import org.ups.citasaludservice.adapter.in.web.api.model.MedicoResponse;
import org.ups.citasaludservice.adapter.in.web.mapper.FranjaHorariaWebMapper;
import org.ups.citasaludservice.adapter.in.web.mapper.MedicoWebMapper;
import org.ups.citasaludservice.application.port.in.ConsultarDisponibilidadUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MedicoController implements MedicosApi {

    private final ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;
    private final MedicoWebMapper medicoWebMapper;
    private final FranjaHorariaWebMapper franjaHorariaWebMapper;

    public MedicoController(ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase,
                             MedicoWebMapper medicoWebMapper,
                             FranjaHorariaWebMapper franjaHorariaWebMapper) {
        this.consultarDisponibilidadUseCase = consultarDisponibilidadUseCase;
        this.medicoWebMapper = medicoWebMapper;
        this.franjaHorariaWebMapper = franjaHorariaWebMapper;
    }

    @Override
    public ResponseEntity<List<MedicoResponse>> listarMedicos(String especialidad) {
        List<MedicoResponse> response = consultarDisponibilidadUseCase
                .listarMedicos(especialidad)
                .stream()
                .map(medicoWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<FranjaHorariaResponse>> obtenerFranjasPorMedicoYFecha(
            UUID medicoId, LocalDate fecha) {
        List<FranjaHorariaResponse> response = consultarDisponibilidadUseCase
                .consultarFranjas(medicoId, fecha)
                .stream()
                .map(franjaHorariaWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
