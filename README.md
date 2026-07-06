# CitaSalud Service

Servicio backend para la reserva de citas medicas en linea. El proyecto expone una API REST para consultar medicos, revisar franjas horarias y registrar citas.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Flyway
- PostgreSQL
- H2 para pruebas
- OpenAPI Generator
- JUnit 5, MockMvc y Cucumber
- JaCoCo

## Arquitectura

El proyecto sigue una estructura cercana a arquitectura hexagonal:

- `domain`: entidades y reglas de negocio
- `application`: casos de uso y puertos
- `adapter/in`: entrada HTTP
- `adapter/out`: persistencia y notificaciones
- `infrastructure`: configuracion de beans

## Funcionalidades

- Listar medicos
- Consultar franjas horarias por medico y fecha
- Reservar una cita
- Consultar una cita por id
- Migraciones automaticas con Flyway
- Contrato API First con OpenAPI

## Estructura

```text
src/main/java/org/ups/citasaludservice
src/main/resources/application.yaml
src/main/resources/openapi/citasalud-api.yml
src/main/resources/db/migration
src/test/java
src/test/resources
specs/001-reserva-cita-online
```

## Requisitos

- JDK 21 o superior
- PostgreSQL ejecutandose en `localhost:5432`
- Base de datos `citasalud`
- Variables de entorno:
  - `DB_USERNAME`
  - `DB_PASSWORD`

## Configuracion

La aplicacion principal usa PostgreSQL y escucha en el puerto `8080`.

Archivo principal de configuracion:

- `src/main/resources/application.yaml`

Configuracion relevante:

- URL: `jdbc:postgresql://localhost:5432/citasalud`
- Flyway habilitado
- `spring.jpa.hibernate.ddl-auto=validate`
- endpoint de salud en `/actuator/health`

## Ejecucion local

```bash
./gradlew bootRun
```

En Windows:

```powershell
.\gradlew.bat bootRun
```

## Pruebas

Ejecutar todas las pruebas:

```bash
./gradlew test
```

Generar cobertura:

```bash
./gradlew check jacocoTestReport
```

El proyecto exige un minimo de cobertura de `80%`.

## API

Contrato OpenAPI:

- `src/main/resources/openapi/citasalud-api.yml`

Endpoints principales:

- `GET /api/v1/medicos`
- `GET /api/v1/medicos/{medicoId}/franjas?fecha=YYYY-MM-DD`
- `POST /api/v1/citas`
- `GET /api/v1/citas/{citaId}`

Ejemplo de reserva:

```json
{
  "medicoId": "a1b2c3d4-0001-0001-0001-000000000001",
  "franjaHorariaId": "c1d2e3f4-0001-0001-0001-000000000001"
}
```

## Base de datos

Migraciones incluidas:

- `V1__create_schema.sql`: crea tablas e indices
- `V2__seed_data.sql`: inserta datos semilla iniciales

Tablas principales:

- `pacientes`
- `medicos`
- `franjas_horarias`
- `citas`

## Pruebas funcionales y especificacion

Documentacion adicional disponible en:

- `specs/001-reserva-cita-online/spec.md`
- `specs/001-reserva-cita-online/plan.md`
- `specs/001-reserva-cita-online/research.md`
- `specs/001-reserva-cita-online/quickstart.md`

## Estado actual y limitaciones

- El contrato OpenAPI declara autenticacion Bearer JWT.
- La implementacion actual todavia no valida JWT real.
- `CitaController` usa temporalmente un `pacienteId` fijo para la reserva.
- El adaptador de WhatsApp solo registra en logs la confirmacion.

## Salud del servicio

```text
GET /actuator/health
```

## Licencia

Proyecto academico de uso interno.
