# language: es
Característica: Reserva de cita médica en línea 24/7
  Como paciente
  Quiero reservar una cita médica en línea en cualquier momento del día
  Para no tener que llamar durante el horario de atención telefónica

  Antecedentes:
    Dado que existe un médico con id "a1b2c3d4-0001-0001-0001-000000000001" en el sistema
    Y que existe un paciente con id "b1c2d3e4-0001-0001-0001-000000000001" en el sistema

  @us1 @reserva-exitosa
  Escenario: Reserva exitosa en franja disponible
    Dado que la franja horaria "c1d2e3f4-0001-0001-0001-000000000001" está disponible
    Cuando el paciente reserva la franja horaria "c1d2e3f4-0001-0001-0001-000000000001"
    Entonces la cita queda registrada con estado "CONFIRMADA"
    Y la franja horaria queda marcada como "OCUPADA"

  @us1 @reserva-fuera-horario
  Escenario: Reserva exitosa fuera del horario de atención telefónica
    Dado que la franja horaria "c1d2e3f4-0002-0002-0002-000000000002" está disponible
    Cuando el paciente reserva la franja horaria "c1d2e3f4-0002-0002-0002-000000000002" a las 2 de la madrugada
    Entonces la cita queda registrada con estado "CONFIRMADA"

  @us2 @franja-ocupada
  Escenario: Rechazo al intentar reservar una franja ya ocupada
    Dado que la franja horaria "c1d2e3f4-0004-0004-0004-000000000004" está ocupada
    Cuando el paciente intenta reservar la franja horaria "c1d2e3f4-0004-0004-0004-000000000004"
    Entonces el sistema rechaza la reserva con error de franja no disponible
    Y el sistema sugiere franjas alternativas disponibles

  @us2 @concurrencia
  Escenario: Rechazo por concurrencia al intentar reservar la misma franja simultáneamente
    Dado que la franja horaria "c1d2e3f4-0003-0003-0003-000000000003" está disponible
    Cuando dos pacientes intentan reservar la misma franja simultáneamente
    Entonces solo una reserva es aceptada
    Y la otra reserva es rechazada con error de franja no disponible
