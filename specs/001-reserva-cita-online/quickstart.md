# Guía de Validación: US-01 · Reserva de Cita en Línea 24/7

**Feature**: `specs/001-reserva-cita-online`
**Date**: 2026-07-05
**Propósito**: Guía ejecutable para verificar que la funcionalidad trabaja de punta a punta.

## Prerrequisitos

- Java 17 instalado y configurado en `$PATH`.
- Gradle Wrapper disponible (`./gradlew`).
- El servicio levantado en `http://localhost:8080` (ver comando de inicio abajo).
- Cliente HTTP disponible: `curl`, Postman, o HTTPie.
- Un JWT válido para un paciente de prueba (ver sección "Datos de prueba").

## Inicio del Servicio

```bash
# Desde la raíz del proyecto
./gradlew bootRun
```

Esperar hasta ver en el log: `Started CitasaludServiceApplication`.
La base de datos H2 en memoria se inicializa automáticamente mediante **Flyway**:

1. `src/main/resources/db/migration/V1__create_schema.sql` — crea las tablas y constraints.
2. `src/main/resources/db/migration/V2__seed_data.sql` — inserta los datos de referencia
   (médicos, especialidades, franjas horarias).

No se requiere ningún paso manual. Flyway aplica las migraciones en orden en cada arranque.

## Datos de Prueba

| Recurso       | ID                                      | Detalle                          |
|---------------|-----------------------------------------|----------------------------------|
| Médico        | `550e8400-e29b-41d4-a716-446655440001`  | Dr. Carlos Mendoza — Medicina General |
| Franja libre  | `660e8400-e29b-41d4-a716-446655440010`  | 2026-07-10 09:00–09:30 DISPONIBLE |
| Franja ocupada| `660e8400-e29b-41d4-a716-446655440011`  | 2026-07-10 09:30–10:00 OCUPADA  |
| JWT paciente  | Ver `src/test/resources/test-token.txt` | Token de prueba pre-generado     |

Reemplaza `$TOKEN` en los comandos siguientes con el contenido de `test-token.txt`.

## Escenario 1: Reserva exitosa (Historia P1, Escenario 1)

**Given**: paciente autenticado; franja DISPONIBLE seleccionada.

```bash
curl -s -X POST http://localhost:8080/api/v1/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "medicoId": "550e8400-e29b-41d4-a716-446655440001",
    "franjaHorariaId": "660e8400-e29b-41d4-a716-446655440010"
  }'
```

**Resultado esperado** (HTTP 201):
```json
{
  "id": "<uuid-generado>",
  "medico": { "nombreCompleto": "Dr. Carlos Mendoza", ... },
  "franjaHoraria": { "horaInicio": "09:00:00", "estado": "OCUPADA" },
  "estado": "CONFIRMADA",
  "fechaCreacion": "<timestamp-actual>"
}
```

**Verificación adicional**: Consultar la franja para confirmar que cambió a `OCUPADA`:

```bash
curl -s http://localhost:8080/api/v1/medicos/550e8400-e29b-41d4-a716-446655440001/franjas?fecha=2026-07-10 \
  -H "Authorization: Bearer $TOKEN"
```

Esperado: la franja `660e8400-...10` aparece con `"estado": "OCUPADA"`.

**Verificación WhatsApp**: Revisar los logs del servicio para confirmar la línea:
```
[WhatsAppNotificacionAdapter] Notificación enviada a +57... para cita <uuid>
```
(En entorno local el adaptador usa un mock que registra en log.)

---

## Escenario 2: Consulta de franjas disponibles (Historia P1, Escenario 2)

**Given**: paciente autenticado consultando agenda del médico.

```bash
curl -s "http://localhost:8080/api/v1/medicos/550e8400-e29b-41d4-a716-446655440001/franjas?fecha=2026-07-10" \
  -H "Authorization: Bearer $TOKEN"
```

**Resultado esperado** (HTTP 200): array con franjas, cada una con `estado: DISPONIBLE | OCUPADA`.
Las franjas `OCUPADA` NO exponen datos del paciente que las reservó.

---

## Escenario 3: Intento de reserva en franja ocupada (Historia P2, Escenario 1)

**Given**: franja `660e8400-...11` ya está `OCUPADA` (cargada en datos de prueba).

```bash
curl -s -X POST http://localhost:8080/api/v1/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "medicoId": "550e8400-e29b-41d4-a716-446655440001",
    "franjaHorariaId": "660e8400-e29b-41d4-a716-446655440011"
  }'
```

**Resultado esperado** (HTTP 409):
```json
{
  "codigo": "FRANJA_NO_DISPONIBLE",
  "mensaje": "La franja horaria seleccionada ya está ocupada.",
  "franjasAlternativas": [ ... ]
}
```

---

## Escenario 4: Reserva en fecha pasada (Caso borde — FR-007)

```bash
curl -s -X POST http://localhost:8080/api/v1/citas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "medicoId": "550e8400-e29b-41d4-a716-446655440001",
    "franjaHorariaId": "<id-de-franja-en-fecha-pasada>"
  }'
```

**Resultado esperado** (HTTP 422):
```json
{
  "codigo": "FECHA_PASADA",
  "mensaje": "No es posible reservar una cita en una fecha u hora pasada."
}
```

---

## Ejecución de Pruebas Automatizadas

```bash
# Pruebas unitarias
./gradlew test

# Pruebas de integración (incluye Spring context slices)
./gradlew integrationTest

# Pruebas funcionales BDD (Cucumber)
./gradlew cucumberTest

# Todas las pruebas + reporte de cobertura JaCoCo
./gradlew check jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html
```

**Umbrales de cobertura esperados** (según constitución, Principio V):
- Cobertura global: ≥ 80%
- Cobertura por clase (dominio + aplicación): > 80%

Si el build falla por umbrales de cobertura, ver `build/reports/jacoco/` para identificar
las clases con cobertura insuficiente.

## Referencias

- Contrato OpenAPI: [`contracts/citasalud-api.yml`](contracts/citasalud-api.yml)
- Modelo de datos: [`data-model.md`](data-model.md)
- Investigación técnica: [`research.md`](research.md)
- Spec completo: [`spec.md`](spec.md)
