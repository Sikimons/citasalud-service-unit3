package org.ups.citasaludservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasaludservice.application.port.out.*;
import org.ups.citasaludservice.domain.exception.FechaPasadaException;
import org.ups.citasaludservice.domain.exception.FranjaNoDisponibleException;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservarCitaInteractorTest {

    @Mock FranjaHorariaRepositoryPort franjaRepo;
    @Mock CitaRepositoryPort citaRepo;
    @Mock PacienteRepositoryPort pacienteRepo;
    @Mock NotificacionGatewayPort notificacionGateway;

    private ReservarCitaInteractor interactor;

    private static final UUID PACIENTE_ID = UUID.randomUUID();
    private static final UUID MEDICO_ID   = UUID.randomUUID();
    private static final UUID FRANJA_ID   = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        interactor = new ReservarCitaInteractor(franjaRepo, citaRepo, pacienteRepo, notificacionGateway);
    }

    @Test
    void given_franja_disponible_when_reservar_then_cita_confirmada() {
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoFranja.DISPONIBLE);
        Paciente paciente = new Paciente(PACIENTE_ID, "Juan Perez", "+593987654321");
        Cita citaGuardada = new Cita(UUID.randomUUID(), PACIENTE_ID, MEDICO_ID, FRANJA_ID,
                EstadoCita.CONFIRMADA, LocalDateTime.now());

        when(franjaRepo.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));
        when(pacienteRepo.buscarPorId(PACIENTE_ID)).thenReturn(Optional.of(paciente));
        when(citaRepo.guardar(any(Cita.class))).thenReturn(citaGuardada);
        when(franjaRepo.guardar(any(FranjaHoraria.class))).thenReturn(franja.marcarOcupada());

        Cita resultado = interactor.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        assertThat(resultado).isNotNull();
        assertThat(resultado.estado()).isEqualTo(EstadoCita.CONFIRMADA);
        verify(franjaRepo).guardar(argThat(f -> f.estado() == EstadoFranja.OCUPADA));
        verify(notificacionGateway).enviarConfirmacion(eq(citaGuardada), eq("+593987654321"));
    }

    @Test
    void given_franja_ocupada_when_reservar_then_lanza_FranjaNoDisponibleException() {
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoFranja.OCUPADA);

        when(franjaRepo.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));

        assertThatThrownBy(() -> interactor.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID))
                .isInstanceOf(FranjaNoDisponibleException.class)
                .extracting(e -> ((FranjaNoDisponibleException) e).getFranjaHorariaId())
                .isEqualTo(FRANJA_ID);

        verify(citaRepo, never()).guardar(any());
        verify(notificacionGateway, never()).enviarConfirmacion(any(), any());
    }

    @Test
    void given_franja_inexistente_when_reservar_then_lanza_exception() {
        when(franjaRepo.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interactor.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_franja_en_fecha_pasada_when_reservar_then_lanza_FechaPasadaException() {
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoFranja.DISPONIBLE);

        when(franjaRepo.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));

        assertThatThrownBy(() -> interactor.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID))
                .isInstanceOf(FechaPasadaException.class);

        verify(citaRepo, never()).guardar(any());
    }

    @Test
    void given_franja_disponible_when_reservar_then_franja_marcada_ocupada() {
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.now().plusDays(7), LocalTime.of(10, 0), LocalTime.of(10, 30),
                EstadoFranja.DISPONIBLE);
        Paciente paciente = new Paciente(PACIENTE_ID, "Maria Lopez", "+593912345678");
        Cita citaGuardada = new Cita(UUID.randomUUID(), PACIENTE_ID, MEDICO_ID, FRANJA_ID,
                EstadoCita.CONFIRMADA, LocalDateTime.now());

        when(franjaRepo.buscarPorIdConBloqueo(FRANJA_ID)).thenReturn(Optional.of(franja));
        when(pacienteRepo.buscarPorId(PACIENTE_ID)).thenReturn(Optional.of(paciente));
        when(citaRepo.guardar(any())).thenReturn(citaGuardada);
        when(franjaRepo.guardar(any())).thenReturn(franja.marcarOcupada());

        interactor.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        verify(franjaRepo).guardar(argThat(f -> f.estado() == EstadoFranja.OCUPADA));
    }
}
