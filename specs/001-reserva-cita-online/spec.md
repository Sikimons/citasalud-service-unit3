# Especificación de Funcionalidad: US-01 · Reserva de Cita en Línea 24/7

**Rama de funcionalidad**: `001-reserva-cita-online`
**Épica**: E-01 | **Puntos de historia**: 8
**Creado**: 2026-07-05
**Estado**: Draft
**Entrada**: Como paciente, quiero reservar una cita en línea en cualquier momento del día,
para no tener que llamar durante mi horario de almuerzo ni acumular intentos fallidos.

## Escenarios de Usuario y Pruebas *(obligatorio)*

<!--
  Historias ordenadas por prioridad. Cada historia es independientemente verificable
  y entregable como incremento MVP.
-->

### Historia de Usuario 1 - Reserva exitosa de cita en cualquier horario (Prioridad: P1)

Como paciente autenticado, quiero reservar una cita médica en línea a cualquier hora del día,
para acceder al servicio sin depender del horario de atención telefónica.

**Por qué esta prioridad**: Es el flujo principal y el valor central de la funcionalidad.
Sin él, ninguna otra historia tiene propósito.

**Prueba independiente**: Se verifica reservando una cita fuera del horario telefónico
(ej. 10 PM) y validando que queda registrada y se recibe confirmación por WhatsApp,
sin depender de ninguna otra historia de usuario.

**Escenarios de aceptación**:

1. **Dado** que el paciente está autenticado y accede al sistema fuera del horario de
   atención telefónica, **Cuando** selecciona un médico, elige una fecha y una franja
   horaria disponible y confirma la reserva, **Entonces** la cita queda registrada en
   el sistema, la franja queda marcada como ocupada y el paciente recibe una confirmación
   por WhatsApp en menos de 60 segundos.

2. **Dado** que el paciente está autenticado y consulta la agenda de un médico,
   **Cuando** selecciona una fecha específica, **Entonces** el sistema muestra las franjas
   horarias disponibles diferenciadas visualmente de las ocupadas, sin exponer datos de
   otros pacientes.

---

### Historia de Usuario 2 - Rechazo de franja horaria ocupada (Prioridad: P2)

Como paciente autenticado, quiero que el sistema me notifique inmediatamente cuando intento
reservar una franja ya ocupada, para elegir otra opción sin frustración ni esperas.

**Por qué esta prioridad**: Protege la integridad de las reservas y la experiencia del
usuario ante condiciones de concurrencia o vistas desactualizadas.

**Prueba independiente**: Se verifica intentando confirmar una franja ya reservada y
validando que el sistema rechaza la operación, muestra el estado actualizado de la franja
y presenta alternativas al paciente.

**Escenarios de aceptación**:

1. **Dado** que el paciente selecciona una franja horaria que está ocupada, **Cuando**
   intenta confirmar la reserva, **Entonces** el sistema muestra un mensaje indicando que
   la franja no está disponible e invita al paciente a elegir otra franja de la misma
   agenda.

2. **Dado** que dos pacientes intentan reservar la misma franja simultáneamente,
   **Cuando** ambos confirman al mismo tiempo, **Entonces** solo un paciente logra
   confirmar la reserva; el otro recibe una notificación de no disponibilidad y se le
   presentan franjas alternativas disponibles.

---

### Casos borde

- ¿Qué pasa si el médico no tiene franjas disponibles para la fecha seleccionada?
  El sistema informa al paciente y sugiere fechas próximas con disponibilidad.
- ¿Qué pasa si el servicio de WhatsApp falla al enviar la confirmación?
  La cita se registra igualmente; el sistema reintenta el envío automáticamente y
  registra el fallo para su seguimiento operativo.
- ¿Qué pasa si el paciente intenta reservar en una fecha u hora pasada?
  El sistema impide la selección y muestra un mensaje de validación.
- ¿Qué pasa si el paciente pierde la conexión durante la confirmación?
  La operación es atómica: la cita se registra completamente o no se registra; no
  quedan estados intermedios.

## Requisitos *(obligatorio)*

### Requisitos Funcionales

- **FR-001**: El sistema DEBE permitir al paciente autenticado consultar la lista de
  médicos disponibles con sus especialidades.
- **FR-002**: El sistema DEBE mostrar las franjas horarias disponibles y ocupadas para
  un médico y fecha seleccionados, sin revelar información de otros pacientes.
- **FR-003**: El sistema DEBE impedir la selección y confirmación de franjas horarias
  marcadas como ocupadas.
- **FR-004**: El sistema DEBE registrar la cita de forma atómica, garantizando que dos
  solicitudes concurrentes sobre la misma franja produzcan exactamente una reserva exitosa
  y un rechazo.
- **FR-005**: El sistema DEBE enviar una notificación de confirmación al paciente por
  WhatsApp en menos de 60 segundos después del registro exitoso de la cita.
- **FR-006**: El sistema DEBE estar disponible para la reserva de citas las 24 horas del
  día, los 7 días de la semana.
- **FR-007**: El sistema DEBE impedir la reserva de citas en fechas u horas anteriores
  al momento actual.
- **FR-008**: El sistema DEBE presentar franjas alternativas disponibles cuando la franja
  seleccionada no pueda ser reservada.

### Entidades Clave

- **Paciente**: Persona que solicita la cita. Atributos clave: identificador único,
  nombre completo, número de WhatsApp verificado.
- **Médico**: Profesional de salud que atiende la cita. Atributos clave: identificador
  único, nombre completo, especialidad.
- **FranjaHoraria**: Bloque de tiempo en la agenda de un médico. Atributos clave: médico
  asociado, fecha, hora de inicio, hora de fin, estado (disponible / ocupada).
- **Cita**: Reserva confirmada entre un paciente y un médico en una franja específica.
  Atributos clave: identificador único, paciente, médico, franja horaria, estado
  (pendiente / confirmada / cancelada), fecha de creación.
- **Notificación**: Mensaje enviado al paciente para confirmar la reserva. Atributos
  clave: tipo (WhatsApp), destinatario, contenido, estado de envío (enviado / fallido /
  pendiente de reintento), timestamp.

## Criterios de Éxito *(obligatorio)*

### Resultados Medibles

- **SC-001**: El paciente puede completar el proceso de reserva (selección de médico,
  fecha y franja, confirmación) en menos de 3 minutos.
- **SC-002**: La confirmación por WhatsApp llega al paciente en menos de 60 segundos
  después de confirmar la reserva exitosamente.
- **SC-003**: El sistema no registra ninguna reserva doble sobre la misma franja horaria
  (0 double bookings) ante solicitudes concurrentes.
- **SC-004**: El sistema mantiene una disponibilidad del 99,9% (menos de 9 horas de
  inactividad al año) sin ventanas de mantenimiento durante horas pico.
- **SC-005**: El 95% de los pacientes que inician el proceso de reserva lo completan
  exitosamente en el primer intento.
- **SC-006**: Las franjas ocupadas son detectadas y rechazadas en el 100% de los intentos
  de reserva concurrente, sin excepción.

## Supuestos

- El paciente está autenticado antes de iniciar el flujo de reserva. La autenticación
  es gestionada por un componente externo a esta funcionalidad.
- Los médicos y sus agendas de disponibilidad (franjas horarias) están precargadas y
  mantenidas en el sistema por el personal administrativo (fuera del alcance de este US).
- La integración con el servicio de mensajería de WhatsApp existe previamente; esta
  funcionalidad únicamente consume dicha integración.
- Una franja horaria tiene duración fija definida por la agenda del médico
  (por ejemplo, 20 o 30 minutos).
- El número de WhatsApp del paciente forma parte de su perfil registrado y está
  verificado con anterioridad.
- El cobro o pago de la cita está fuera del alcance de esta historia de usuario.
- La disponibilidad 24/7 del sistema aplica únicamente al canal en línea; el horario
  de atención telefónica es un proceso separado e independiente.
