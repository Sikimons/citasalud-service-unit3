CREATE TABLE pacientes (
    id              UUID        NOT NULL ,
    nombre_completo VARCHAR(255) NOT NULL,
    numero_whatsapp VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_pacientes PRIMARY KEY (id),
    CONSTRAINT uk_pacientes_whatsapp UNIQUE (numero_whatsapp)
);

CREATE TABLE medicos (
    id              UUID        NOT NULL ,
    nombre_completo VARCHAR(255) NOT NULL,
    especialidad    VARCHAR(100) NOT NULL,
    CONSTRAINT pk_medicos PRIMARY KEY (id)
);

CREATE TABLE franjas_horarias (
    id          UUID        NOT NULL ,
    medico_id   UUID        NOT NULL,
    fecha       DATE        NOT NULL,
    hora_inicio TIME        NOT NULL,
    hora_fin    TIME        NOT NULL,
    estado      VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT pk_franjas_horarias PRIMARY KEY (id),
    CONSTRAINT uk_medico_fecha_hora_inicio UNIQUE (medico_id, fecha, hora_inicio),
    CONSTRAINT fk_franjas_medico FOREIGN KEY (medico_id) REFERENCES medicos(id)
);

CREATE TABLE citas (
    id                UUID        NOT NULL ,
    paciente_id       UUID        NOT NULL,
    medico_id         UUID        NOT NULL,
    franja_horaria_id UUID        NOT NULL,
    estado            VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    fecha_creacion    TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT pk_citas PRIMARY KEY (id),
    CONSTRAINT fk_citas_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    CONSTRAINT fk_citas_medico   FOREIGN KEY (medico_id)   REFERENCES medicos(id),
    CONSTRAINT fk_citas_franja   FOREIGN KEY (franja_horaria_id) REFERENCES franjas_horarias(id)
);

CREATE INDEX idx_franjas_medico_fecha ON franjas_horarias(medico_id, fecha);
CREATE INDEX idx_citas_paciente       ON citas(paciente_id);
CREATE INDEX idx_citas_medico         ON citas(medico_id);
