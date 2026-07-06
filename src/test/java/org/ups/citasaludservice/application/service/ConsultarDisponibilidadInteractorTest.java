package org.ups.citasaludservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.application.port.out.MedicoRepositoryPort;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarDisponibilidadInteractorTest {

    @Mock MedicoRepositoryPort medicoRepo;
    @Mock FranjaHorariaRepositoryPort franjaRepo;

    private ConsultarDisponibilidadInteractor interactor;

    private static final UUID MEDICO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        interactor = new ConsultarDisponibilidadInteractor(medicoRepo, franjaRepo);
    }

    @Test
    void given_especialidad_when_listarMedicos_then_devuelve_medicos_filtrados() {
        Medico medico = new Medico(MEDICO_ID, "Dra. Ana García", "Cardiología");
        when(medicoRepo.buscarPorEspecialidad("Cardiología")).thenReturn(List.of(medico));

        List<Medico> resultado = interactor.listarMedicos("Cardiología");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).especialidad()).isEqualTo("Cardiología");
    }

    @Test
    void given_sin_filtro_when_listarMedicos_then_devuelve_todos() {
        List<Medico> todos = List.of(
                new Medico(UUID.randomUUID(), "Dr. Carlos Ramos", "Medicina General"),
                new Medico(UUID.randomUUID(), "Dra. Laura Pérez", "Pediatría")
        );
        when(medicoRepo.buscarTodos()).thenReturn(todos);

        List<Medico> resultado = interactor.listarMedicos(null);

        assertThat(resultado).hasSize(2);
        verify(medicoRepo).buscarTodos();
        verify(medicoRepo, never()).buscarPorEspecialidad(any());
    }

    @Test
    void given_medico_y_fecha_when_consultarFranjasDisponibles_then_devuelve_solo_disponibles() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        List<FranjaHoraria> disponibles = List.of(
                new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, fecha,
                        LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE)
        );
        when(franjaRepo.buscarDisponiblesPorMedicoYFecha(MEDICO_ID, fecha)).thenReturn(disponibles);

        List<FranjaHoraria> resultado = interactor.consultarFranjasDisponibles(MEDICO_ID, fecha);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estaDisponible()).isTrue();
    }

    @Test
    void given_medico_y_fecha_when_consultarFranjas_then_devuelve_todas_las_franjas() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        List<FranjaHoraria> todas = List.of(
                new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, fecha,
                        LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE),
                new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, fecha,
                        LocalTime.of(9, 30), LocalTime.of(10, 0), EstadoFranja.OCUPADA)
        );
        when(franjaRepo.buscarPorMedicoYFecha(MEDICO_ID, fecha)).thenReturn(todas);

        List<FranjaHoraria> resultado = interactor.consultarFranjas(MEDICO_ID, fecha);

        assertThat(resultado).hasSize(2);
    }
}
