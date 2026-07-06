package org.ups.citasaludservice.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ups.citasaludservice.adapter.in.web.api.model.ReservarCitaRequest;
import org.ups.citasaludservice.adapter.in.web.mapper.CitaWebMapper;
import org.ups.citasaludservice.adapter.in.web.mapper.FranjaHorariaWebMapper;
import org.ups.citasaludservice.application.port.in.ReservarCitaUseCase;
import org.ups.citasaludservice.application.port.out.CitaRepositoryPort;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.application.port.out.MedicoRepositoryPort;
import org.ups.citasaludservice.domain.exception.FechaPasadaException;
import org.ups.citasaludservice.domain.exception.FranjaNoDisponibleException;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CitaController.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class CitaControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private ReservarCitaUseCase reservarCitaUseCase;
    @MockitoBean private CitaRepositoryPort citaRepositoryPort;
    @MockitoBean private MedicoRepositoryPort medicoRepositoryPort;
    @MockitoBean private FranjaHorariaRepositoryPort franjaHorariaRepositoryPort;
    @MockitoBean private CitaWebMapper citaWebMapper;
    @MockitoBean private FranjaHorariaWebMapper franjaHorariaWebMapper;

    private static final UUID MEDICO_ID  = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
    private static final UUID FRANJA_ID  = UUID.fromString("c1d2e3f4-0001-0001-0001-000000000001");
    private static final UUID CITA_ID    = UUID.randomUUID();

    @Test
    void given_franja_disponible_when_POST_citas_then_devuelve_201() throws Exception {
        Cita cita = new Cita(CITA_ID,
                UUID.fromString("b1c2d3e4-0001-0001-0001-000000000001"),
                MEDICO_ID, FRANJA_ID, EstadoCita.CONFIRMADA, LocalDateTime.now());
        Medico medico = new Medico(MEDICO_ID, "Dra. Ana García", "Cardiología");
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);

        when(reservarCitaUseCase.reservar(any(), any(), any())).thenReturn(cita);
        when(medicoRepositoryPort.buscarPorId(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(franjaHorariaRepositoryPort.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));

        ReservarCitaRequest request = new ReservarCitaRequest()
                .medicoId(MEDICO_ID)
                .franjaHorariaId(FRANJA_ID);

        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/citas/" + CITA_ID));
    }

    @Test
    void given_franja_ocupada_when_POST_citas_then_devuelve_409() throws Exception {
        when(reservarCitaUseCase.reservar(any(), any(), any()))
                .thenThrow(new FranjaNoDisponibleException(FRANJA_ID));
        when(franjaHorariaRepositoryPort.buscarPorIdConBloqueo(FRANJA_ID))
                .thenReturn(Optional.empty());
        when(franjaHorariaRepositoryPort.buscarDisponiblesPorMedicoYFecha(any(), any()))
                .thenReturn(List.of());

        ReservarCitaRequest request = new ReservarCitaRequest()
                .medicoId(MEDICO_ID)
                .franjaHorariaId(FRANJA_ID);

        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FRANJA_NO_DISPONIBLE"));
    }

    @Test
    void given_franja_ocupada_con_alternativas_when_POST_then_devuelve_409_con_sugerencias() throws Exception {
        UUID altFranjaId = UUID.fromString("c1d2e3f4-0002-0002-0002-000000000002");
        FranjaHoraria franjaOcupada = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);
        FranjaHoraria altFranja = new FranjaHoraria(altFranjaId, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 30), LocalTime.of(10, 0), EstadoFranja.DISPONIBLE);

        when(reservarCitaUseCase.reservar(any(), any(), any()))
                .thenThrow(new FranjaNoDisponibleException(FRANJA_ID));
        when(franjaHorariaRepositoryPort.buscarPorIdConBloqueo(FRANJA_ID))
                .thenReturn(Optional.of(franjaOcupada));
        when(franjaHorariaRepositoryPort.buscarDisponiblesPorMedicoYFecha(MEDICO_ID, franjaOcupada.fecha()))
                .thenReturn(List.of(altFranja));

        ReservarCitaRequest request = new ReservarCitaRequest()
                .medicoId(MEDICO_ID)
                .franjaHorariaId(FRANJA_ID);

        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FRANJA_NO_DISPONIBLE"));
    }

    @Test
    void given_cita_existente_when_GET_cita_then_devuelve_200() throws Exception {
        Cita cita = new Cita(CITA_ID,
                UUID.fromString("b1c2d3e4-0001-0001-0001-000000000001"),
                MEDICO_ID, FRANJA_ID, EstadoCita.CONFIRMADA, LocalDateTime.now());
        Medico medico = new Medico(MEDICO_ID, "Dra. Ana García", "Cardiología");
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);

        when(citaRepositoryPort.buscarPorId(CITA_ID)).thenReturn(Optional.of(cita));
        when(medicoRepositoryPort.buscarPorId(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(franjaHorariaRepositoryPort.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));
        when(citaWebMapper.toDto(any(), any(), any())).thenReturn(new org.ups.citasaludservice.adapter.in.web.api.model.CitaResponse()
                .id(CITA_ID)
                .estado(org.ups.citasaludservice.adapter.in.web.api.model.CitaResponse.EstadoEnum.CONFIRMADA));

        mockMvc.perform(get("/api/v1/citas/{citaId}", CITA_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CITA_ID.toString()));
    }

    @Test
    void given_cita_no_encontrada_when_GET_cita_then_devuelve_404() throws Exception {
        when(citaRepositoryPort.buscarPorId(CITA_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/citas/{citaId}", CITA_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("RECURSO_NO_ENCONTRADO"));
    }

    @Test
    void given_fecha_pasada_when_POST_citas_then_devuelve_422() throws Exception {
        when(reservarCitaUseCase.reservar(any(), any(), any()))
                .thenThrow(new FechaPasadaException());

        ReservarCitaRequest request = new ReservarCitaRequest()
                .medicoId(MEDICO_ID)
                .franjaHorariaId(FRANJA_ID);

        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("FECHA_PASADA"));
    }
}
