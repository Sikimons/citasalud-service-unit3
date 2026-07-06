package org.ups.citasaludservice.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.ups.citasaludservice.adapter.out.persistence.mapper.CitaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.mapper.FranjaHorariaPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.mapper.MedicoPersistenceMapper;
import org.ups.citasaludservice.adapter.out.persistence.repository.CitaJpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.repository.FranjaHorariaJpaRepository;
import org.ups.citasaludservice.adapter.out.persistence.repository.MedicoJpaRepository;
import org.ups.citasaludservice.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/db/testdata/test-setup.sql")
@Import({CitaJpaAdapter.class, CitaPersistenceMapper.class,
         FranjaHorariaJpaAdapter.class, FranjaHorariaPersistenceMapper.class,
         MedicoJpaAdapter.class, MedicoPersistenceMapper.class})
class CitaJpaAdapterIntegrationTest {

    @Autowired
    private CitaJpaAdapter citaJpaAdapter;

    @Autowired
    private FranjaHorariaJpaAdapter franjaHorariaJpaAdapter;

    @Autowired
    private MedicoJpaAdapter medicoJpaAdapter;

    @Autowired
    private CitaJpaRepository citaJpaRepository;

    @Autowired
    private FranjaHorariaJpaRepository franjaHorariaJpaRepository;

    @Autowired
    private MedicoJpaRepository medicoJpaRepository;

    @Test
    void given_cita_nueva_when_guardar_then_persiste_y_recupera() {
        // Arrange: use seeded medico/paciente IDs
        UUID medicoId   = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
        UUID pacienteId = UUID.fromString("b1c2d3e4-0001-0001-0001-000000000001");
        UUID franjaId   = UUID.fromString("c1d2e3f4-0001-0001-0001-000000000001");

        Cita nuevaCita = new Cita(null, pacienteId, medicoId, franjaId,
                EstadoCita.CONFIRMADA, LocalDateTime.now());

        // Act
        Cita guardada = citaJpaAdapter.guardar(nuevaCita);

        // Assert
        assertThat(guardada.id()).isNotNull();
        Optional<Cita> encontrada = citaJpaAdapter.buscarPorId(guardada.id());
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().estado()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    void given_franja_disponible_when_buscarPorId_then_devuelve_franja() {
        UUID franjaId = UUID.fromString("c1d2e3f4-0001-0001-0001-000000000001");

        Optional<FranjaHoraria> resultado = franjaHorariaJpaAdapter.buscarPorId(franjaId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().estaDisponible()).isTrue();
    }

    @Test
    void given_franja_disponible_when_guardar_ocupada_then_estado_actualizado() {
        UUID franjaId = UUID.fromString("c1d2e3f4-0002-0002-0002-000000000002");
        FranjaHoraria franja = franjaHorariaJpaAdapter.buscarPorId(franjaId).orElseThrow();

        FranjaHoraria ocupada = franja.marcarOcupada();
        franjaHorariaJpaAdapter.guardar(ocupada);

        FranjaHoraria recuperada = franjaHorariaJpaAdapter.buscarPorId(franjaId).orElseThrow();
        assertThat(recuperada.estado()).isEqualTo(EstadoFranja.OCUPADA);
    }

    @Test
    void given_franjas_when_buscarPorMedicoYFecha_then_devuelve_todas() {
        UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");

        var franjas = franjaHorariaJpaAdapter.buscarPorMedicoYFecha(medicoId, LocalDate.now().plusDays(7));
        assertThat(franjas).isNotEmpty();
    }

    @Test
    void given_franjas_when_buscarDisponibles_then_devuelve_solo_disponibles() {
        UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");

        var disponibles = franjaHorariaJpaAdapter.buscarDisponiblesPorMedicoYFecha(
                medicoId, LocalDate.now().plusDays(7));
        assertThat(disponibles).allMatch(FranjaHoraria::estaDisponible);
    }

    @Test
    void given_medicos_seeded_when_buscarTodos_then_devuelve_lista() {
        var medicos = medicoJpaAdapter.buscarTodos();
        assertThat(medicos).isNotEmpty();
    }

    @Test
    void given_medico_existente_when_buscarPorId_then_encontrado() {
        UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
        var medico = medicoJpaAdapter.buscarPorId(medicoId);
        assertThat(medico).isPresent();
    }

    @Test
    void given_medico_por_especialidad_when_buscar_then_encontrado() {
        var medicos = medicoJpaAdapter.buscarPorEspecialidad("Cardiologia");
        assertThat(medicos).isNotEmpty();
    }
}
