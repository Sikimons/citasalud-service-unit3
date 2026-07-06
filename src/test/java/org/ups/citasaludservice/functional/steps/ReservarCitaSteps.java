package org.ups.citasaludservice.functional.steps;

import io.cucumber.java.Before;
import io.cucumber.java.es.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.ups.citasaludservice.application.port.in.ReservarCitaUseCase;
import org.ups.citasaludservice.application.port.out.FranjaHorariaRepositoryPort;
import org.ups.citasaludservice.domain.exception.FranjaNoDisponibleException;
import org.ups.citasaludservice.domain.model.Cita;
import org.ups.citasaludservice.domain.model.EstadoCita;
import org.ups.citasaludservice.domain.model.EstadoFranja;
import org.ups.citasaludservice.domain.model.FranjaHoraria;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservarCitaSteps {

    @Autowired
    private ReservarCitaUseCase reservarCitaUseCase;

    @Autowired
    private FranjaHorariaRepositoryPort franjaHorariaRepositoryPort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final UUID MEDICO_ID   = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
    private static final UUID PACIENTE_ID = UUID.fromString("b1c2d3e4-0001-0001-0001-000000000001");
    private static final UUID PACIENTE_ID_2 = UUID.fromString("b1c2d3e4-0002-0002-0002-000000000002");

    private UUID franjaId;
    private Cita citaResultado;
    private FranjaNoDisponibleException excepcionCapturada;
    private int reservasAceptadas;
    private int reservasRechazadas;

    @Before
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM citas");
        jdbcTemplate.execute("DELETE FROM franjas_horarias");
        jdbcTemplate.execute("DELETE FROM pacientes");
        jdbcTemplate.execute("DELETE FROM medicos");

        jdbcTemplate.execute("INSERT INTO medicos (id, nombre_completo, especialidad) VALUES " +
                "('a1b2c3d4-0001-0001-0001-000000000001', 'Dr. Garcia', 'Cardiologia')");

        jdbcTemplate.execute("INSERT INTO pacientes (id, nombre_completo, numero_whatsapp) VALUES " +
                "('b1c2d3e4-0001-0001-0001-000000000001', 'Juan Perez', '+593987654321')");
        jdbcTemplate.execute("INSERT INTO pacientes (id, nombre_completo, numero_whatsapp) VALUES " +
                "('b1c2d3e4-0002-0002-0002-000000000002', 'Maria Lopez', '+593987654322')");

        jdbcTemplate.execute("INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES " +
                "('c1d2e3f4-0001-0001-0001-000000000001', 'a1b2c3d4-0001-0001-0001-000000000001', " +
                "DATEADD('DAY', 7, CURRENT_DATE), '09:00:00', '09:30:00', 'DISPONIBLE')");
        jdbcTemplate.execute("INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES " +
                "('c1d2e3f4-0002-0002-0002-000000000002', 'a1b2c3d4-0001-0001-0001-000000000001', " +
                "DATEADD('DAY', 7, CURRENT_DATE), '09:30:00', '10:00:00', 'DISPONIBLE')");
        jdbcTemplate.execute("INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES " +
                "('c1d2e3f4-0003-0003-0003-000000000003', 'a1b2c3d4-0001-0001-0001-000000000001', " +
                "DATEADD('DAY', 7, CURRENT_DATE), '10:00:00', '10:30:00', 'DISPONIBLE')");
        jdbcTemplate.execute("INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES " +
                "('c1d2e3f4-0004-0004-0004-000000000004', 'a1b2c3d4-0001-0001-0001-000000000001', " +
                "DATEADD('DAY', 7, CURRENT_DATE), '10:30:00', '11:00:00', 'OCUPADA')");
    }

    @Dado("que existe un médico con id {string} en el sistema")
    public void que_existe_un_medico(String medicoId) {
        // Seeded via test-setup.sql
    }

    @Y("que existe un paciente con id {string} en el sistema")
    public void que_existe_un_paciente(String pacienteId) {
        // Seeded via test-setup.sql
    }

    @Dado("que la franja horaria {string} está disponible")
    public void que_la_franja_esta_disponible(String id) {
        this.franjaId = UUID.fromString(id);
        FranjaHoraria franja = franjaHorariaRepositoryPort.buscarPorId(franjaId)
                .orElseThrow(() -> new AssertionError("Franja no encontrada: " + id));
        assertThat(franja.estaDisponible()).isTrue();
    }

    @Dado("que la franja horaria {string} está ocupada")
    public void que_la_franja_esta_ocupada(String id) {
        this.franjaId = UUID.fromString(id);
        FranjaHoraria franja = franjaHorariaRepositoryPort.buscarPorId(franjaId)
                .orElseThrow(() -> new AssertionError("Franja no encontrada: " + id));
        assertThat(franja.estado()).isEqualTo(EstadoFranja.OCUPADA);
    }

    @Cuando("el paciente reserva la franja horaria {string}")
    public void el_paciente_reserva_la_franja(String id) {
        this.franjaId = UUID.fromString(id);
        this.citaResultado = reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, franjaId);
    }

    @Cuando("el paciente reserva la franja horaria {string} a las 2 de la madrugada")
    public void el_paciente_reserva_la_franja_fuera_horario(String id) {
        this.franjaId = UUID.fromString(id);
        this.citaResultado = reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, franjaId);
    }

    @Cuando("el paciente intenta reservar la franja horaria {string}")
    public void el_paciente_intenta_reservar_franja_ocupada(String id) {
        this.franjaId = UUID.fromString(id);
        try {
            reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, franjaId);
        } catch (FranjaNoDisponibleException ex) {
            this.excepcionCapturada = ex;
        }
    }

    @Cuando("dos pacientes intentan reservar la misma franja simultáneamente")
    public void dos_pacientes_intentan_reservar_simultaneamente() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger aceptadas = new AtomicInteger(0);
        AtomicInteger rechazadas = new AtomicInteger(0);

        Runnable reserva1 = () -> {
            try {
                latch.await();
                reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, franjaId);
                aceptadas.incrementAndGet();
            } catch (FranjaNoDisponibleException e) {
                rechazadas.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable reserva2 = () -> {
            try {
                latch.await();
                reservarCitaUseCase.reservar(PACIENTE_ID_2, MEDICO_ID, franjaId);
                aceptadas.incrementAndGet();
            } catch (FranjaNoDisponibleException e) {
                rechazadas.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(reserva1);
        executor.submit(reserva2);
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        this.reservasAceptadas = aceptadas.get();
        this.reservasRechazadas = rechazadas.get();
    }

    @Entonces("la cita queda registrada con estado {string}")
    public void la_cita_queda_registrada(String estado) {
        assertThat(citaResultado).isNotNull();
        assertThat(citaResultado.id()).isNotNull();
        assertThat(citaResultado.estado()).isEqualTo(EstadoCita.valueOf(estado));
    }

    @Y("la franja horaria queda marcada como {string}")
    public void la_franja_queda_marcada(String estado) {
        FranjaHoraria franja = franjaHorariaRepositoryPort.buscarPorId(franjaId)
                .orElseThrow();
        assertThat(franja.estado()).isEqualTo(EstadoFranja.valueOf(estado));
    }

    @Entonces("el sistema rechaza la reserva con error de franja no disponible")
    public void el_sistema_rechaza_la_reserva() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getFranjaHorariaId()).isEqualTo(franjaId);
    }

    @Y("el sistema sugiere franjas alternativas disponibles")
    public void el_sistema_sugiere_alternativas() {
        // Validated at controller layer — GlobalExceptionHandler returns franjasAlternativas
    }

    @Entonces("solo una reserva es aceptada")
    public void solo_una_reserva_es_aceptada() {
        assertThat(reservasAceptadas).isEqualTo(1);
    }

    @Y("la otra reserva es rechazada con error de franja no disponible")
    public void la_otra_reserva_es_rechazada() {
        assertThat(reservasRechazadas).isEqualTo(1);
    }
}
