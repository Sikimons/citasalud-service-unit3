# Plan de Implementación: US-01 · Reserva de Cita en Línea 24/7

**Branch**: `001-reserva-cita-online` | **Date**: 2026-07-05 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/001-reserva-cita-online/spec.md`

## Summary

Implementar la reserva de citas médicas en línea 24/7 para el servicio `citasalud-service`.
El flujo principal permite al paciente autenticado seleccionar médico, fecha y franja
horaria disponible, confirmar la reserva (con garantía de atomicidad ante concurrencia)
y recibir confirmación por WhatsApp. La arquitectura sigue Clean Architecture (Robert
Martin) con Spring Boot 4.1.0 + Java 17 + Gradle, API First con openapi-generator,
pruebas BDD con JUnit 5 + Cucumber-JVM, y gates de cobertura JaCoCo.

## Technical Context

**Language/Version**: Java 17 (LTS)

**Primary Dependencies**:
- Spring Boot 4.1.0 (Spring Web MVC, Spring Data JPA)
- Flyway (migraciones de esquema de base de datos — versionadas y aplicadas automáticamente)
- Lombok (reducción de boilerplate en adaptadores y DTOs)
- openapi-generator-gradle-plugin 7.x (generación de stubs desde contrato OpenAPI)
- JUnit 5 + Mockito + AssertJ (pruebas unitarias)
- Testcontainers (pruebas de integración con BD real, cuando se migre de H2)
- Cucumber-JVM (cucumber-spring + cucumber-junit-platform-engine) (pruebas BDD funcionales)
- JaCoCo (reporte y gates de cobertura)

**Storage**: H2 en memoria (desarrollo/test); arquitectura preparada para PostgreSQL en
producción mediante configuración externa. El esquema y los datos de pre-carga se gestionan
con **Flyway**: los scripts viven en `src/main/resources/db/migration/` y se aplican
automáticamente en el arranque. Los datos de prueba específicos de test se ubican en
`src/test/resources/db/testdata/` y se cargan vía configuración del perfil de pruebas.

**Testing**:
- Unitarias: JUnit 5 + Mockito — `./gradlew test`
- Integración: Spring Boot test slices + Testcontainers — `./gradlew integrationTest`
- Funcionales BDD: Cucumber-JVM — `./gradlew cucumberTest`
- Cobertura: JaCoCo — `./gradlew check jacocoTestReport`

**Target Platform**: JVM / Linux server (contenedor Docker)

**Project Type**: web-service (REST API)

**Performance Goals**:
- Proceso de reserva completo < 3 minutos (SC-001)
- Notificación WhatsApp < 60 segundos tras la confirmación (SC-002)
- 99.9% de disponibilidad (SC-004)

**Constraints**:
- 0 reservas dobles bajo concurrencia (SC-003, SC-006)
- El código generado por openapi-generator debe excluirse de los cálculos de cobertura
- Sin ventanas de mantenimiento durante horas pico

**Scale/Scope**: Sistema de salud con acceso concurrente de pacientes. El bloqueo
pesimista garantiza la integridad de las reservas.

## Constitution Check

*GATE: Debe pasar antes de Phase 0. Re-verificar tras Phase 1.*

### I. Clean Architecture ✅

- [x] Capas separadas: `domain` → `application` → `adapter` → `infrastructure`
- [x] Entidades de dominio: POJOs sin anotaciones JPA/Spring
- [x] Casos de uso: independientemente testeables sin contexto Spring
- [x] Interfaces de repositorio/gateway definidas en `application/port/out/`
- [x] Adaptadores de persistencia y mensajería en `adapter/out/`
- [x] Controladores REST en `adapter/in/web/` implementando interfaces generadas

### II. BDD Testing ✅

- [x] Pruebas unitarias con JUnit 5 + Mockito — nomenclatura `given_X_when_Y_then_Z`
- [x] Pruebas de integración con Spring Boot slices + Testcontainers
- [x] Pruebas funcionales con Cucumber-JVM — feature files en Gherkin
- [x] Tests escritos antes de la implementación (Red → Green → Refactor)
- [x] Escenarios mapean 1:1 con los criterios de aceptación del spec.md

### III. SOLID + YAGNI + DRY ✅

- [x] Un caso de uso = un interactor (Single Responsibility)
- [x] Puertos de entrada separados de puertos de salida (Interface Segregation)
- [x] Solo se implementan las 2 historias de usuario del sprint (YAGNI)
- [x] Lógica de reserva atómica centralizada en `ReservarCitaInteractor` (DRY)
- [x] Sin abstracciones especulativas ni parámetros sin usar

### IV. API First + OpenAPI ✅

- [x] Contrato `contracts/citasalud-api.yml` revisado antes de escribir código
- [x] openapi-generator-gradle-plugin configurado para generar interfaces del servidor
- [x] Controladores implementan interfaces generadas (no extienden clases base)
- [x] Ruta del contrato: `src/main/resources/openapi/citasalud-api.yml`

### V. JaCoCo Coverage Gates ✅

- [x] `jacocoTestCoverageVerification` configurado con umbrales global ≥ 80% y por clase > 80%
- [x] Código generado excluido vía patrones de exclusión JaCoCo
- [x] Build falla automáticamente si los umbrales no se cumplen

**Resultado**: Sin violaciones. No se requiere Complexity Tracking.

## Project Structure

### Documentation (this feature)

```text
specs/001-reserva-cita-online/
├── plan.md              # Este archivo (/speckit-plan)
├── research.md          # Decisiones técnicas de Phase 0
├── data-model.md        # Modelo de dominio de Phase 1
├── quickstart.md        # Guía de validación de Phase 1
├── contracts/
│   └── citasalud-api.yml  # Contrato OpenAPI 3.x (fuente de verdad)
├── checklists/
│   └── requirements.md  # Checklist de calidad del spec
└── tasks.md             # Phase 2 — generado por /speckit-tasks
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/org/ups/citasaludservice/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Cita.java
│   │   │   │   ├── FranjaHoraria.java
│   │   │   │   ├── Medico.java
│   │   │   │   ├── Paciente.java
│   │   │   │   ├── Notificacion.java
│   │   │   │   ├── EstadoFranja.java
│   │   │   │   ├── EstadoCita.java
│   │   │   │   ├── EstadoEnvio.java
│   │   │   │   └── TipoNotificacion.java
│   │   │   └── exception/
│   │   │       ├── FranjaNoDisponibleException.java
│   │   │       └── FechaPasadaException.java
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── in/
│   │   │   │   │   ├── ReservarCitaUseCase.java
│   │   │   │   │   └── ConsultarDisponibilidadUseCase.java
│   │   │   │   └── out/
│   │   │   │       ├── CitaRepositoryPort.java
│   │   │   │       ├── FranjaHorariaRepositoryPort.java
│   │   │   │       ├── MedicoRepositoryPort.java
│   │   │   │       └── NotificacionGatewayPort.java
│   │   │   └── usecase/
│   │   │       ├── ReservarCitaInteractor.java
│   │   │       └── ConsultarDisponibilidadInteractor.java
│   │   ├── adapter/
│   │   │   ├── in/
│   │   │   │   └── web/
│   │   │   │       ├── CitaController.java        # implements CitasApi (generada)
│   │   │   │       ├── MedicoController.java      # implements MedicosApi (generada)
│   │   │   │       └── mapper/
│   │   │   │           ├── CitaWebMapper.java
│   │   │   │           └── MedicoWebMapper.java
│   │   │   └── out/
│   │   │       ├── persistence/
│   │   │       │   ├── entity/
│   │   │       │   │   ├── CitaEntity.java
│   │   │       │   │   ├── FranjaHorariaEntity.java
│   │   │       │   │   ├── MedicoEntity.java
│   │   │       │   │   └── PacienteEntity.java
│   │   │       │   ├── repository/
│   │   │       │   │   ├── CitaJpaRepository.java
│   │   │       │   │   ├── FranjaHorariaJpaRepository.java
│   │   │       │   │   └── MedicoJpaRepository.java
│   │   │       │   ├── adapter/
│   │   │       │   │   ├── CitaJpaAdapter.java
│   │   │       │   │   ├── FranjaHorariaJpaAdapter.java
│   │   │       │   │   └── MedicoJpaAdapter.java
│   │   │       │   └── mapper/
│   │   │       │       ├── CitaPersistenceMapper.java
│   │   │       │       └── FranjaHorariaPersistenceMapper.java
│   │   │       └── messaging/
│   │   │           └── WhatsAppNotificacionAdapter.java
│   │   └── infrastructure/
│   │       └── config/
│   │           └── BeanConfiguration.java
│   └── resources/
│       ├── application.yaml
│       ├── openapi/
│       │   └── citasalud-api.yml                # Copia del contrato para generación
│       └── db/
│           ├── migration/
│           │   ├── V1__create_schema.sql         # DDL: creación de tablas y constraints
│           │   └── V2__seed_data.sql             # DML: datos pre-cargados de referencia
│           └── testdata/
│               └── R__test_seed.sql              # Datos repetibles de prueba (solo test profile)
├── test/
│   ├── java/org/ups/citasaludservice/
│   │   ├── unit/
│   │   │   ├── domain/
│   │   │   │   └── model/
│   │   │   └── application/
│   │   │       ├── usecase/
│   │   │       │   ├── ReservarCitaInteractorTest.java
│   │   │       │   └── ConsultarDisponibilidadInteractorTest.java
│   │   │       └── port/
│   │   ├── integration/
│   │   │   ├── adapter/
│   │   │   │   ├── persistence/
│   │   │   │   │   └── CitaJpaAdapterIntegrationTest.java
│   │   │   │   └── web/
│   │   │   │       ├── CitaControllerIntegrationTest.java
│   │   │   │       └── MedicoControllerIntegrationTest.java
│   │   │   └── messaging/
│   │   │       └── WhatsAppAdapterIntegrationTest.java
│   │   └── functional/
│   │       ├── CucumberTestRunner.java
│   │       └── steps/
│   │           ├── ReservarCitaSteps.java
│   │           └── FranjaOcupadaSteps.java
│   └── resources/
│       ├── application-test.yaml
│       ├── db/
│       │   └── testdata/
│       │       └── R__test_seed.sql              # Datos repetibles cargados en cada test run
│       └── features/
│           ├── reservar_cita.feature
│           └── franja_ocupada.feature
```

**Structure Decision**: Single-project Spring Boot web service con Clean Architecture.
El directorio `adapter/in/web/` contiene únicamente código "puente" (implementaciones de
las interfaces generadas y mappers). Todo el razonamiento de negocio reside en `domain/`
y `application/`. Los DTOs de la API son generados por openapi-generator y viven en
`build/generated/` — no en `src/`.

**Decisión de DB**: Los scripts de base de datos se organizan bajo `resources/db/` en dos
niveles:
- `migration/` — scripts Flyway versionados (`V{n}__descripcion.sql`) aplicados en producción,
  test e integración. `V1` crea el esquema completo; `V2` inserta los datos de referencia
  del dominio (especialidades, médicos iniciales, franjas configuradas).
- `testdata/` — script repetible Flyway (`R__test_seed.sql`) cargado únicamente en el
  perfil de pruebas (`application-test.yaml` configura `spring.flyway.locations` para incluir
  `classpath:db/testdata`). Permite aislar datos de prueba del seed de producción sin duplicar
  el esquema. Spring Boot aplica Flyway automáticamente en el arranque; no se requiere
  `data.sql` ni `schema.sql` separados.

## Complexity Tracking

> Sin violaciones de constitución que justificar.
