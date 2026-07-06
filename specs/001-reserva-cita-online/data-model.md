# Modelo de Datos: US-01 · Reserva de Cita en Línea 24/7

**Feature**: `specs/001-reserva-cita-online`
**Date**: 2026-07-05
**Capa**: Dominio (POJOs puros — sin anotaciones de framework)

## Entidades de Dominio

### Paciente

Representa al usuario que solicita la cita médica.

| Campo          | Tipo     | Restricciones                          |
|----------------|----------|----------------------------------------|
| id             | UUID     | No nulo, único, generado por el sistema |
| nombreCompleto | String   | No nulo, 2–150 caracteres              |
| numeroWhatsApp | String   | No nulo, formato E.164 (+573001234567) |

**Invariantes de dominio**:
- `numeroWhatsApp` debe cumplir el formato internacional E.164.
- Un `Paciente` existe previamente (creado por el módulo de autenticación/registro, fuera del alcance de este US).

---

### Medico

Representa al profesional de salud que atiende la cita.

| Campo          | Tipo     | Restricciones                          |
|----------------|----------|----------------------------------------|
| id             | UUID     | No nulo, único                         |
| nombreCompleto | String   | No nulo, 2–150 caracteres              |
| especialidad   | String   | No nulo, valor de catálogo             |

**Invariantes de dominio**:
- La `especialidad` debe pertenecer a una lista de valores definidos por el negocio.
- Un `Medico` y sus agendas existen previamente (cargados por el módulo administrativo).

---

### FranjaHoraria

Bloque de tiempo en la agenda de un médico. Es el recurso sobre el que compiten los pacientes.

| Campo      | Tipo           | Restricciones                                       |
|------------|----------------|-----------------------------------------------------|
| id         | UUID           | No nulo, único                                      |
| medicoId   | UUID           | No nulo, referencia a `Medico`                      |
| fecha      | LocalDate      | No nulo, no puede ser pasada                        |
| horaInicio | LocalTime      | No nulo                                             |
| horaFin    | LocalTime      | No nulo, `horaFin > horaInicio`                     |
| estado     | EstadoFranja   | No nulo, valor inicial: `DISPONIBLE`                |

**Enumeración EstadoFranja**:
- `DISPONIBLE` — la franja puede ser reservada.
- `OCUPADA` — la franja ya tiene una cita confirmada.

**Invariantes de dominio**:
- `horaFin` DEBE ser mayor que `horaInicio`.
- Una `FranjaHoraria` no puede volver de `OCUPADA` a `DISPONIBLE` directamente (requiere cancelación de la cita asociada, fuera del alcance de este US).
- La transición `DISPONIBLE → OCUPADA` es atómica y se protege con bloqueo pesimista.

**Transiciones de estado**:
```
DISPONIBLE ──(reserva confirmada)──▶ OCUPADA
```

---

### Cita

Reserva confirmada entre un paciente y un médico en una franja horaria.

| Campo           | Tipo         | Restricciones                                     |
|-----------------|--------------|---------------------------------------------------|
| id              | UUID         | No nulo, único, generado por el sistema           |
| pacienteId      | UUID         | No nulo, referencia a `Paciente`                  |
| medicoId        | UUID         | No nulo, referencia a `Medico`                    |
| franjaHorariaId | UUID         | No nulo, referencia a `FranjaHoraria`             |
| estado          | EstadoCita   | No nulo, valor inicial: `CONFIRMADA`              |
| fechaCreacion   | LocalDateTime| No nulo, asignado en el momento de la reserva     |

**Enumeración EstadoCita**:
- `CONFIRMADA` — cita reservada exitosamente.
- `CANCELADA` — cita anulada (fuera del alcance de este US).

**Invariantes de dominio**:
- Una `Cita` solo puede crearse si su `FranjaHoraria` está en estado `DISPONIBLE`.
- La creación de `Cita` y el cambio de `FranjaHoraria` a `OCUPADA` ocurren en la misma transacción atómica.
- No puede existir más de una `Cita` confirmada para la misma `FranjaHoraria`.

---

### Notificacion

Registro del mensaje de confirmación enviado al paciente por WhatsApp.

| Campo        | Tipo              | Restricciones                                  |
|--------------|-------------------|------------------------------------------------|
| id           | UUID              | No nulo, único                                 |
| tipo         | TipoNotificacion  | No nulo                                        |
| destinatario | String            | No nulo, número WhatsApp en formato E.164      |
| contenido    | String            | No nulo, texto del mensaje de confirmación     |
| estadoEnvio  | EstadoEnvio       | No nulo, valor inicial: `PENDIENTE`            |
| timestamp    | LocalDateTime     | No nulo, momento de creación del registro      |

**Enumeración TipoNotificacion**:
- `WHATSAPP`

**Enumeración EstadoEnvio**:
- `PENDIENTE` — en cola de envío.
- `ENVIADA` — entregada al proveedor de WhatsApp.
- `FALLIDA` — error en el envío; pendiente de reintento.

**Transiciones de estado**:
```
PENDIENTE ──(envío exitoso)──▶ ENVIADA
PENDIENTE ──(error de envío)──▶ FALLIDA
FALLIDA   ──(reintento exitoso)──▶ ENVIADA
```

---

## Relaciones entre Entidades

```
Medico ──(1)──< FranjaHoraria  (un médico tiene muchas franjas)
FranjaHoraria ──(1)──(0..1) Cita  (una franja tiene como máximo una cita)
Paciente ──(1)──< Cita         (un paciente puede tener muchas citas)
Cita ──(1)──< Notificacion     (una cita genera al menos una notificación)
```

## Separación de capas: Dominio vs. Persistencia

Las entidades de dominio son **POJOs puros** (sin anotaciones JPA/Spring).
Las entidades JPA viven en `adapter/out/persistence/entity/` y tienen sus propias
anotaciones (`@Entity`, `@Id`, `@Version`, `@Lock`, etc.).

Los mapeadores (mappers) en la capa de adaptador de persistencia convierten entre
el modelo de dominio y las entidades JPA. Esto garantiza que el dominio no dependa
nunca del framework de persistencia (Principio I de la constitución).

## Restricción de base de datos para prevención de reservas dobles

Además del bloqueo pesimista en código, se define una restricción UNIQUE a nivel de BD:

```sql
ALTER TABLE franja_horaria
    ADD CONSTRAINT uq_franja_ocupada
    UNIQUE (medico_id, fecha, hora_inicio);
```

Esta restricción actúa como defensa en profundidad: si el bloqueo pesimista falla
(por error de configuración), la BD rechaza la segunda inserción/actualización.
