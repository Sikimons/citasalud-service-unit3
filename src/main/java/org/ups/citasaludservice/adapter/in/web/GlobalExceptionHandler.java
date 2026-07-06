package org.ups.citasaludservice.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ups.citasaludservice.adapter.in.web.api.model.ErrorResponse;
import org.ups.citasaludservice.adapter.in.web.api.model.FranjaHorariaResponse;
import org.ups.citasaludservice.adapter.in.web.mapper.FranjaHorariaWebMapper;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.domain.exception.FechaPasadaException;
import org.ups.citasaludservice.domain.exception.FranjaNoDisponibleException;
import org.ups.citasaludservice.domain.model.FranjaHoraria;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final FranjaHorariaRepositoryPort franjaRepo;
    private final FranjaHorariaWebMapper franjaMapper;

    public GlobalExceptionHandler(FranjaHorariaRepositoryPort franjaRepo,
                                   FranjaHorariaWebMapper franjaMapper) {
        this.franjaRepo = franjaRepo;
        this.franjaMapper = franjaMapper;
    }

    @ExceptionHandler(FranjaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleFranjaNoDisponible(FranjaNoDisponibleException ex) {
        UUID medicoId = resolverMedicoId(ex.getFranjaHorariaId());
        List<FranjaHorariaResponse> alternativas = resolverAlternativas(ex.getFranjaHorariaId(), medicoId);

        ErrorResponse response = new ErrorResponse()
                .codigo("FRANJA_NO_DISPONIBLE")
                .mensaje("La franja horaria seleccionada ya está ocupada.")
                .franjasAlternativas(alternativas);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(FechaPasadaException.class)
    public ResponseEntity<ErrorResponse> handleFechaPasada(FechaPasadaException ex) {
        ErrorResponse response = new ErrorResponse()
                .codigo("FECHA_PASADA")
                .mensaje(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse()
                .codigo("RECURSO_NO_ENCONTRADO")
                .mensaje(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private UUID resolverMedicoId(UUID franjaId) {
        return franjaRepo.buscarPorIdConBloqueo(franjaId)
                .map(FranjaHoraria::medicoId)
                .orElse(null);
    }

    private List<FranjaHorariaResponse> resolverAlternativas(UUID franjaId, UUID medicoId) {
        if (medicoId == null) return List.of();
        return franjaRepo.buscarPorIdConBloqueo(franjaId)
                .map(franja -> franjaRepo.buscarDisponiblesPorMedicoYFecha(medicoId, franja.fecha()))
                .orElse(List.of())
                .stream()
                .filter(f -> !f.id().equals(franjaId))
                .map(franjaMapper::toDto)
                .collect(Collectors.toList());
    }
}
