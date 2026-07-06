---
description: "Task list for US-01 Â· Reserva de Cita en LÃ­nea 24/7"
---

# Tareas: US-01 Â· Reserva de Cita en LÃ­nea 24/7

**Input**: Design documents from `specs/001-reserva-cita-online/`

**Prerequisites**: plan.md âœ… | spec.md âœ… | research.md âœ… | data-model.md âœ… | contracts/ âœ…

**Tests**: Incluidos â€” requeridos por el Principio II de la constituciÃ³n (BDD Testing).
Cada historia tiene pruebas unitarias (JUnit5 + Mockito), de integraciÃ³n (Spring Boot slices)
y funcionales (Cucumber-JVM). Secuencia obligatoria: Tests Red â†’ ImplementaciÃ³n â†’ Green â†’ Refactor.

**OrganizaciÃ³n**: Tareas agrupadas por historia de usuario para implementaciÃ³n y prueba
independiente de cada una.

## Formato: `[ID] [P?] [Story?] DescripciÃ³n con ruta de archivo`

- **[P]**: Ejecutable en paralelo (archivos diferentes, sin dependencias pendientes)
- **[Story]**: Historia de usuario ([US1], [US2]) â€” solo en fases de historias
- Rutas relativas a la raÃ­z del repositorio

## Convenciones de rutas

- ProducciÃ³n Java: `src/main/java/org/ups/citasaludservice/`
- Recursos producciÃ³n: `src/main/resources/`
- Pruebas Java: `src/test/java/org/ups/citasaludservice/`
- Recursos pruebas: `src/test/resources/`

---

## Phase 1: Setup (Infraestructura del Proyecto)

**PropÃ³sito**: ConfiguraciÃ³n inicial â€” dependencias, plugins, generaciÃ³n de cÃ³digo y
estructura de base de datos con Flyway.

- [X] T001 Actualizar `build.gradle` con dependencias: `org.flywaydb:flyway-core`, `io.cucumber:cucumber-spring`, `io.cucumber:cucumber-junit-platform-engine`, Mockito, AssertJ, Testcontainers, plugin `org.openapi.generator` 7.x, plugin JaCoCo
- [X] T002 [P] Copiar contrato OpenAPI desde `specs/001-reserva-cita-online/contracts/citasalud-api.yml` a `src/main/resources/openapi/citasalud-api.yml`
- [X] T003 Configurar tarea `openApiGenerate` en `build.gradle`: `generatorName="spring"`, `interfaceOnly=true`, `useSpringBoot3=true`, `apiPackage="org.ups.citasaludservice.adapter.in.web.api"`, `modelPackage="org.ups.citasaludservice.adapter.in.web.dto"` (depende de T001)
- [X] T004 [P] Configurar plugin JaCoCo en `build.gradle`: `jacocoTestCoverageVerification` con lÃ­mite global `minimum=0.80` y lÃ­mite por clase `minimum=0.80`; excluir paquetes `*.adapter.in.web.dto.*` y `*.adapter.in.web.api.*` (depende de T001)
- [X] T005 Ejecutar `./gradlew openApiGenerate` y verificar que las interfaces `CitasApi.java` y `MedicosApi.java` se generan en `build/generated/` sin errores de compilaciÃ³n (depende de T003)

---

## Phase 2: Fundacional (Prerrequisitos Bloqueantes)

**PropÃ³sito**: Dominio, puertos, entidades JPA, scripts Flyway y estructura compartida
que DEBE completarse antes de iniciar cualquier historia de usuario.

**âš ï¸ CRÃTICO**: Ninguna historia puede comenzar hasta que esta fase estÃ© completa.

- [X] T006 [P] Crear enumeraciones `EstadoFranja.java`, `EstadoCita.java`, `EstadoEnvio.java`, `TipoNotificacion.java` en `src/main/java/org/ups/citasaludservice/domain/model/`
- [X] T007 [P] Crear domain record `Paciente.java` (id: UUID, nombreCompleto: String, numeroWhatsApp: String) en `src/main/java/org/ups/citasaludservice/domain/model/` â€” POJO puro sin anotaciones de framework
- [X] T008 [P] Crear domain record `Medico.java` (id: UUID, nombreCompleto: String, especialidad: String) en `src/main/java/org/ups/citasaludservice/domain/model/` â€” POJO puro
- [X] T009 [P] Crear domain record `FranjaHoraria.java` (id: UUID, medicoId: UUID, fecha: LocalDate, horaInicio: LocalTime, horaFin: LocalTime, estado: EstadoFranja) con validaciÃ³n `horaFin > horaInicio` en `src/main/java/org/ups/citasaludservice/domain/model/`
- [X] T010 [P] Crear domain record `Cita.java` (id: UUID, pacienteId: UUID, medicoId: UUID, franjaHorariaId: UUID, estado: EstadoCita, fechaCreacion: LocalDateTime) en `src/main/java/org/ups/citasaludservice/domain/model/`
- [X] T011 [P] Crear domain record `Notificacion.java` (id: UUID, tipo: TipoNotificacion, destinatario: String, contenido: String, estadoEnvio: EstadoEnvio, timestamp: LocalDateTime) en `src/main/java/org/ups/citasaludservice/domain/model/`
- [X] T012 [P] Crear excepciones de dominio `FranjaNoDisponibleException.java` y `FechaPasadaException.java` en `src/main/java/org/ups/citasaludservice/domain/exception/`
- [X] T013 [P] Crear puertos de entrada `ReservarCitaUseCase.java` y `ConsultarDisponibilidadUseCase.java` en `src/main/java/org/ups/citasaludservice/application/port/in/`
- [X] T014 [P] Crear puertos de salida `CitaRepositoryPort.java`, `FranjaHorariaRepositoryPort.java`, `MedicoRepositoryPort.java` en `src/main/java/org/ups/citasaludservice/application/port/out/`
- [X] T015 [P] Crear puerto de salida `NotificacionGatewayPort.java` con mÃ©todo `enviarConfirmacion(Cita cita)` en `src/main/java/org/ups/citasaludservice/application/port/out/`
- [X] T016 [P] Crear entidades JPA `CitaEntity.java` y `FranjaHorariaEntity.java` con anotaciones `@Entity`, `@Id`, `@Version` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/entity/`
- [X] T017 [P] Crear entidades JPA `MedicoEntity.java` y `PacienteEntity.java` con anotaciones `@Entity`, `@Id` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/entity/`
- [X] T018 [P] Crear interfaces Spring Data JPA `CitaJpaRepository.java` y `MedicoJpaRepository.java` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/repository/`
- [X] T019 [P] Crear interfaz `FranjaHorariaJpaRepository.java` con mÃ©todo `findByIdForUpdate` anotado con `@Lock(LockModeType.PESSIMISTIC_WRITE)` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/repository/`
- [X] T020 Crear script Flyway `src/main/resources/db/migration/V1__create_schema.sql` con DDL completo: tablas `paciente`, `medico`, `franja_horaria` (con `UNIQUE(medico_id, fecha, hora_inicio)`), `cita`, `notificacion`; tipos, constraints e Ã­ndices segÃºn `data-model.md`
- [X] T021 Crear script Flyway `src/main/resources/db/migration/V2__seed_data.sql` con datos de referencia pre-cargados: mÃ©dico Dr. Carlos Mendoza (UUID `550e8400-e29b-41d4-a716-446655440001`, especialidad Medicina General) y franjas horarias para 2026-07-10 09:00â€“10:00 (una DISPONIBLE, una OCUPADA)
- [X] T022 [P] Crear script Flyway repetible `src/test/resources/db/testdata/R__test_seed.sql` con datos aislados para pruebas: paciente de prueba, mÃ©dico de prueba y franjas en fechas futuras (cargado solo en perfil de test)
- [X] T023 [P] Crear `src/main/resources/application.yaml` con H2 en memoria y `spring.flyway.locations=classpath:db/migration`; crear `src/test/resources/application-test.yaml` con `spring.flyway.locations=classpath:db/migration,classpath:db/testdata`
- [X] T024 [P] Crear `BeanConfiguration.java` en `src/main/java/org/ups/citasaludservice/infrastructure/config/` conectando casos de uso con sus puertos mediante inyecciÃ³n de dependencias en Spring
- [X] T025 Crear `CucumberTestRunner.java` con anotaciones `@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features")` y `@ConfigurationParameter(GLUE_PROPERTY_NAME, "org.ups.citasaludservice.functional")` en `src/test/java/org/ups/citasaludservice/functional/`

**Checkpoint**: FundaciÃ³n lista â€” las historias de usuario pueden comenzar en paralelo.

---

## Phase 3: Historia de Usuario 1 - Reserva Exitosa en Cualquier Horario (Prioridad: P1) ðŸŽ¯ MVP

**Objetivo**: El paciente autenticado selecciona mÃ©dico, fecha y franja disponible, confirma
la reserva (atomicidad garantizada) y recibe confirmaciÃ³n por WhatsApp.

**Prueba independiente**: Ejecutar `./gradlew test cucumberTest` â€” todos los tests de US1
pasan. Validar manualmente con `curl` los Escenarios 1 y 2 del `quickstart.md`.

### Tests para US1 (BDD â€” escribir ANTES de implementar) âš ï¸

> **Verificar que estos tests FALLAN antes de continuar con la implementaciÃ³n (fase Red)**

- [X] T026 [P] [US1] Crear `src/test/resources/features/reservar_cita.feature` con escenarios Gherkin de US1: reserva exitosa fuera de horario telefÃ³nico y consulta de franjas disponibles/ocupadas
- [X] T027 [P] [US1] Crear `ReservarCitaSteps.java` step definitions en `src/test/java/org/ups/citasaludservice/functional/steps/` mapeando los pasos Gherkin de US1 (deben fallar â€” fase Red)
- [X] T028 [P] [US1] Crear `ReservarCitaInteractorTest.java` en `src/test/java/org/ups/citasaludservice/unit/application/usecase/` con mÃ©todos BDD: `given_franjaDisponible_when_reservarCita_then_citaConfirmadaYFranjaMarcadaOcupada()` y `given_pacienteAutenticado_when_consultarFranjas_then_retornaFranjasConEstado()` (deben fallar â€” fase Red)
- [X] T029 [P] [US1] Crear `ConsultarDisponibilidadInteractorTest.java` en `src/test/java/org/ups/citasaludservice/unit/application/usecase/` con escenarios BDD para consulta de franjas por mÃ©dico y fecha (deben fallar â€” fase Red)
- [X] T030 [P] [US1] Crear `MedicoControllerIntegrationTest.java` con `@WebMvcTest` en `src/test/java/org/ups/citasaludservice/integration/adapter/web/` verificando `GET /api/v1/medicos` y `GET /api/v1/medicos/{id}/franjas?fecha=` (deben fallar â€” fase Red)
- [X] T031 [P] [US1] Crear `CitaJpaAdapterIntegrationTest.java` con `@DataJpaTest` en `src/test/java/org/ups/citasaludservice/integration/adapter/persistence/` verificando persistencia de Cita y transiciÃ³n de FranjaHoraria a OCUPADA (deben fallar â€” fase Red)

### ImplementaciÃ³n de US1

- [X] T032 [P] [US1] Implementar `ConsultarDisponibilidadInteractor.java` en `src/main/java/org/ups/citasaludservice/application/usecase/` implementando `ConsultarDisponibilidadUseCase` â€” lista mÃ©dicos y retorna franjas con estado (depende de T013, T014)
- [X] T033 [P] [US1] Implementar mappers de persistencia `CitaPersistenceMapper.java` y `FranjaHorariaPersistenceMapper.java` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/mapper/` (dominio â†” JPA entity) (depende de T016, T017)
- [X] T034 [US1] Implementar `FranjaHorariaJpaAdapter.java` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/adapter/` con bloqueo pesimista `@Lock(PESSIMISTIC_WRITE)` al obtener franja para reserva (depende de T019, T033)
- [X] T035 [P] [US1] Implementar `CitaJpaAdapter.java` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/adapter/` (depende de T018, T033)
- [X] T036 [P] [US1] Implementar `MedicoJpaAdapter.java` en `src/main/java/org/ups/citasaludservice/adapter/out/persistence/adapter/` (depende de T018, T033)
- [X] T037 [P] [US1] Implementar `WhatsAppNotificacionAdapter.java` con `@Async` â€” stub que loguea el envÃ­o â€” en `src/main/java/org/ups/citasaludservice/adapter/out/messaging/` (depende de T015)
- [X] T038 [US1] Implementar `ReservarCitaInteractor.java` en `src/main/java/org/ups/citasaludservice/application/usecase/`: valida disponibilidad con bloqueo pesimista, crea Cita, cambia FranjaHoraria a OCUPADA en transacciÃ³n atÃ³mica, dispara notificaciÃ³n async (depende de T013, T014, T032, T034)
- [X] T039 [P] [US1] Implementar mappers web `CitaWebMapper.java` y `MedicoWebMapper.java` en `src/main/java/org/ups/citasaludservice/adapter/in/web/mapper/` (dominio â†” DTOs generados por openapi-generator) (depende de T005)
- [X] T040 [US1] Implementar `MedicoController.java` implementando la interfaz `MedicosApi` generada en `src/main/java/org/ups/citasaludservice/adapter/in/web/` (depende de T039, T032)
- [X] T041 [US1] Implementar `CitaController.java` implementando la interfaz `CitasApi` generada en `src/main/java/org/ups/citasaludservice/adapter/in/web/` (depende de T039, T038, T040)
- [X] T042 [US1] Verificar que todos los tests de US1 pasan â€” ejecutar `./gradlew test cucumberTest` (fase Green)
- [X] T043 [US1] Refactorizar cÃ³digo US1 manteniendo todos los tests verdes: revisar duplicaciÃ³n, nomenclatura y separaciÃ³n estricta de capas (fase Refactor)

**Checkpoint**: US1 completamente funcional y verificable de forma independiente.

---

## Phase 4: Historia de Usuario 2 - Rechazo de Franja Horaria Ocupada (Prioridad: P2)

**Objetivo**: El sistema rechaza reservas sobre franjas ocupadas (incluida concurrencia
simultÃ¡nea), retorna HTTP 409 con franjas alternativas y valida fechas pasadas con HTTP 422.

**Prueba independiente**: Ejecutar `./gradlew test cucumberTest` â€” tests de US2 pasan.
Validar con `curl` los Escenarios 3 y 4 del `quickstart.md`.

### Tests para US2 (BDD â€” escribir ANTES de implementar) âš ï¸

> **Verificar que estos tests FALLAN antes de continuar con la implementaciÃ³n (fase Red)**

- [X] T044 [P] [US2] Crear `src/test/resources/features/franja_ocupada.feature` con escenarios Gherkin de US2: franja ya ocupada y dos pacientes simultÃ¡neos intentando la misma franja
- [X] T045 [P] [US2] Crear `FranjaOcupadaSteps.java` step definitions en `src/test/java/org/ups/citasaludservice/functional/steps/` mapeando los pasos Gherkin de US2 (deben fallar â€” fase Red)
- [X] T046 [P] [US2] Agregar mÃ©todos de concurrencia a `ReservarCitaInteractorTest.java` en `src/test/java/org/ups/citasaludservice/unit/application/usecase/`: `given_franjaOcupada_when_reservarCita_then_lanzaFranjaNoDisponibleException()` y `given_dosPacientesConcurrentes_when_reservanMismaFranja_then_soloUnaTieneExito()` (deben fallar â€” fase Red)
- [X] T047 [P] [US2] Crear `CitaControllerConflictIntegrationTest.java` con `@WebMvcTest` en `src/test/java/org/ups/citasaludservice/integration/adapter/web/` verificando HTTP 409 con `franjasAlternativas` y HTTP 422 para fecha pasada (deben fallar â€” fase Red)

### ImplementaciÃ³n de US2

- [X] T048 [US2] Agregar validaciÃ³n de fecha pasada en `ReservarCitaInteractor.java` en `src/main/java/org/ups/citasaludservice/application/usecase/`: lanzar `FechaPasadaException` si `franjaHoraria.fecha()` es anterior a `LocalDate.now()` (depende de T038)
- [X] T049 [US2] Agregar lÃ³gica de franjas alternativas en `ReservarCitaInteractor.java` en `src/main/java/org/ups/citasaludservice/application/usecase/`: al capturar `FranjaNoDisponibleException`, consultar franjas disponibles del mismo mÃ©dico y fecha para incluirlas en la respuesta (FR-008) (depende de T038, T032)
- [X] T050 [P] [US2] Implementar `GlobalExceptionHandler.java` con `@RestControllerAdvice` en `src/main/java/org/ups/citasaludservice/adapter/in/web/`: mapear `FranjaNoDisponibleException` â†’ HTTP 409 con `ErrorResponse` y `franjasAlternativas`; `FechaPasadaException` â†’ HTTP 422 con `ErrorResponse`
- [X] T051 [US2] Verificar que todos los tests de US1 y US2 pasan â€” ejecutar `./gradlew test cucumberTest` (fase Green)
- [X] T052 [US2] Refactorizar cÃ³digo US2 manteniendo todos los tests verdes: verificar que la lÃ³gica de franjas alternativas no viola DRY ni las capas de Clean Architecture (fase Refactor)

**Checkpoint**: US1 y US2 completamente funcionales e independientemente verificables.

---

## Phase 5: Polish y Preocupaciones Transversales

**PropÃ³sito**: ValidaciÃ³n final de calidad, cobertura JaCoCo y cumplimiento de la constituciÃ³n.

- [X] T053 [P] Ejecutar `./gradlew check jacocoTestReport` y verificar cobertura global â‰¥ 80% y por clase > 80% â€” abrir `build/reports/jacoco/test/html/index.html` y revisar clases en rojo
- [X] T054 [P] Ejecutar los 4 escenarios de validaciÃ³n del `quickstart.md` con `curl` contra el servicio corriendo en `http://localhost:8080` â€” todos deben retornar los cÃ³digos HTTP esperados
- [X] T055 [P] Auditar que ninguna clase en `src/main/java/.../domain/` ni en `.../application/` contiene anotaciones de framework (`@Entity`, `@Component`, `@Autowired`, `@Service`, etc.) â€” cumplimiento del Principio I de la constituciÃ³n
- [X] T056 Ejecutar build final `./gradlew build` â€” debe completarse sin errores, con todos los checks JaCoCo y tests pasando

---

## Dependencias y Orden de EjecuciÃ³n

### Dependencias entre Fases

- **Setup (Phase 1)**: Sin dependencias â€” puede comenzar inmediatamente.
- **Fundacional (Phase 2)**: Requiere que T005 (`openApiGenerate`) complete â€” BLOQUEA todas las historias.
- **US1 (Phase 3)**: Puede comenzar tras Phase 2 completa.
- **US2 (Phase 4)**: Requiere `ReservarCitaInteractor` (T038) y `ConsultarDisponibilidadInteractor` (T032) de US1.
- **Polish (Phase 5)**: Requiere US1 y US2 completas.

### Dependencias entre Historias

- **US1 (P1)**: Sin dependencias en US2 â€” puede entregarse como MVP independiente.
- **US2 (P2)**: Extiende T038 y T032 de US1. No reemplaza funcionalidad existente.

### Secuencia dentro de Cada Historia

```
Tests [P] (escribir, confirmar que FALLAN)
  â†’ Mappers [P] (persistencia + web)
  â†’ Adaptadores de persistencia (dependen de mappers)
  â†’ Adaptador de mensajerÃ­a [P] (independiente)
  â†’ Interactor principal (depende de adaptadores)
  â†’ Controladores (dependen de interactor + mappers web)
  â†’ VerificaciÃ³n Green (./gradlew test)
  â†’ Refactor
```

### Oportunidades de Paralelismo

- **Phase 2**: T006â€“T025 â€” todos los marcados `[P]` pueden ejecutarse simultÃ¡neamente.
- **US1 tests** (T026â€“T031): todos en paralelo entre sÃ­.
- **US1 adaptadores** (T033â€“T037): mappers y adaptadores en paralelo.
- **US2 tests** (T044â€“T047): todos en paralelo entre sÃ­.
- **Polish** (T053â€“T055): tres verificaciones en paralelo.

---

## Ejemplo de Paralelismo: Historia de Usuario 1

```bash
# Escribir todos los tests de US1 en paralelo (deben FALLAR):
Tarea T026: "Crear reservar_cita.feature con escenarios Gherkin US1"
Tarea T027: "Crear ReservarCitaSteps.java step definitions"
Tarea T028: "Crear ReservarCitaInteractorTest.java con naming BDD"
Tarea T029: "Crear ConsultarDisponibilidadInteractorTest.java"
Tarea T030: "Crear MedicoControllerIntegrationTest.java"
Tarea T031: "Crear CitaJpaAdapterIntegrationTest.java"

# Implementar adaptadores en paralelo (tras T033 mapper):
Tarea T034: "Implementar FranjaHorariaJpaAdapter con lock pesimista"
Tarea T035: "Implementar CitaJpaAdapter"
Tarea T036: "Implementar MedicoJpaAdapter"
Tarea T037: "Implementar WhatsAppNotificacionAdapter (@Async stub)"
Tarea T039: "Implementar CitaWebMapper y MedicoWebMapper"
```

---

## Estrategia de ImplementaciÃ³n

### MVP Primero (Solo US1)

1. Completar Phase 1: Setup
2. Completar Phase 2: Fundacional (**CRÃTICO** â€” bloquea todo)
3. Completar Phase 3: US1
4. **PARAR Y VALIDAR**: Tests + Escenarios 1 y 2 del `quickstart.md`
5. Demo: flujo completo de reserva funcionando 24/7

### Entrega Incremental

1. Phase 1 + Phase 2 â†’ Base lista
2. Phase 3 (US1) â†’ Probar â†’ **Demo MVP** con reserva exitosa
3. Phase 4 (US2) â†’ Probar â†’ **Demo** con manejo de conflictos
4. Phase 5 (Polish) â†’ Build final con cobertura â‰¥ 80%

### Estrategia con Equipo Paralelo (tras Phase 2 completa)

- Dev A: Tests US1 (T026â€“T031) + ImplementaciÃ³n US1 (T032â€“T043)
- Dev B: Tests US2 (T044â€“T047) mientras Dev A implementa â€” espera T038 para implementar US2
- Juntos: Phase 5 â€” verificaciÃ³n final

---

## Notas

- `[P]` = archivos diferentes, sin dependencias pendientes â€” ejecutar en paralelo
- `[US1]`/`[US2]` mapea cada tarea a su historia de usuario para trazabilidad
- Confirmar que los tests **FALLAN** antes de implementar (Red-Green-Refactor obligatorio)
- Hacer commit tras cada tarea o grupo lÃ³gico
- Detenerse en cada Checkpoint para validar la historia de forma independiente
- El cÃ³digo en `domain/` y `application/` debe ser libre de anotaciones de framework en todo momento
- Flyway aplica migraciones automÃ¡ticamente â€” no modificar tablas manualmente en ningÃºn entorno
