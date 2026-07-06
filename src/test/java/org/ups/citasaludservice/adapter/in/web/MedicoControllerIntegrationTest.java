package org.ups.citasaludservice.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ups.citasaludservice.adapter.in.web.api.model.FranjaHorariaResponse;
import org.ups.citasaludservice.adapter.in.web.api.model.MedicoResponse;
import org.ups.citasaludservice.application.port.in.ConsultarDisponibilidadUseCase;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.adapter.in.web.mapper.MedicoWebMapper;
import org.ups.citasaludservice.adapter.in.web.mapper.FranjaHorariaWebMapper;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicoController.class)
@ActiveProfiles("test")
class MedicoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;

    @MockitoBean
    private MedicoWebMapper medicoWebMapper;

    @MockitoBean
    private FranjaHorariaWebMapper franjaHorariaWebMapper;

    @MockitoBean
    private FranjaHorariaRepositoryPort franjaHorariaRepositoryPort;

    @Test
    void given_medicos_existentes_when_GET_medicos_then_devuelve_200() throws Exception {
        List<Medico> medicos = List.of(
                new Medico(UUID.randomUUID(), "Dra. Ana García", "Cardiología")
        );
        when(consultarDisponibilidadUseCase.listarMedicos(null)).thenReturn(medicos);
        when(medicoWebMapper.toDto(medicos.get(0))).thenReturn(
                new MedicoResponse()
                        .id(medicos.get(0).id())
                        .nombreCompleto("Dra. Ana García")
                        .especialidad("Cardiología")
        );

        mockMvc.perform(get("/api/v1/medicos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombreCompleto").value("Dra. Ana García"));
    }

    @Test
    void given_sin_medicos_when_GET_medicos_then_devuelve_lista_vacia() throws Exception {
        when(consultarDisponibilidadUseCase.listarMedicos(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/medicos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void given_franjas_when_GET_franjas_then_devuelve_200() throws Exception {
        UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
        UUID franjaId = UUID.fromString("c1d2e3f4-0001-0001-0001-000000000001");
        LocalDate fecha = LocalDate.now().plusDays(7);

        FranjaHoraria franja = new FranjaHoraria(franjaId, medicoId, fecha,
                LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE);
        FranjaHorariaResponse dto = new FranjaHorariaResponse()
                .id(franjaId)
                .horaInicio("09:00:00")
                .horaFin("09:30:00")
                .estado(FranjaHorariaResponse.EstadoEnum.DISPONIBLE);

        when(consultarDisponibilidadUseCase.consultarFranjas(any(), any()))
                .thenReturn(List.of(franja));
        when(franjaHorariaWebMapper.toDto(franja)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/medicos/{medicoId}/franjas", medicoId)
                        .param("fecha", fecha.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));
    }

    @Test
    void given_sin_franjas_when_GET_franjas_then_devuelve_lista_vacia() throws Exception {
        UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");

        when(consultarDisponibilidadUseCase.consultarFranjas(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/medicos/{medicoId}/franjas", medicoId)
                        .param("fecha", LocalDate.now().plusDays(7).toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
