-- V1__create_tables.sql
-- Local: src/main/resources/db/migration/
-- MySQL: AUTO_INCREMENT, ENGINE=InnoDB, TINYINT(1) para boolean

-- ============================================================
-- TABELA: convenios
-- ============================================================
CREATE TABLE IF NOT EXISTS convenios (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    registro   VARCHAR(20)  UNIQUE,
    ativo      TINYINT(1)   NOT NULL DEFAULT 1,
    criado_em  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABELA: especialidades
-- ============================================================
CREATE TABLE IF NOT EXISTS especialidades (
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    nome  VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABELA: pacientes
-- ============================================================
CREATE TABLE IF NOT EXISTS pacientes (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    nome             VARCHAR(100) NOT NULL,
    cpf              VARCHAR(11)  NOT NULL UNIQUE,
    email            VARCHAR(150) UNIQUE,
    telefone         VARCHAR(11),
    data_nascimento  DATE,
    sexo             VARCHAR(20),
    -- Campos de Endereco (@Embeddable - ficam na mesma tabela)
    logradouro       VARCHAR(150),
    numero           VARCHAR(10),
    complemento      VARCHAR(80),
    bairro           VARCHAR(80),
    cidade           VARCHAR(80),
    uf               VARCHAR(2),
    cep              VARCHAR(8),
    convenio_id      BIGINT,
    ativo            TINYINT(1)   NOT NULL DEFAULT 1,
    criado_em        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em    DATETIME     ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_paciente_convenio FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_pacientes_nome ON pacientes(nome);

-- ============================================================
-- TABELA: medicos
-- ============================================================
CREATE TABLE IF NOT EXISTS medicos (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(100) NOT NULL,
    crm       VARCHAR(7)   NOT NULL UNIQUE,
    email     VARCHAR(150) UNIQUE,
    telefone  VARCHAR(11),
    ativo     TINYINT(1)   NOT NULL DEFAULT 1,
    criado_em DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABELA: medico_especialidade (relacao Muitos para Muitos)
-- ============================================================
CREATE TABLE IF NOT EXISTS medico_especialidade (
    medico_id        BIGINT NOT NULL,
    especialidade_id BIGINT NOT NULL,
    PRIMARY KEY (medico_id, especialidade_id),
    CONSTRAINT fk_me_medico        FOREIGN KEY (medico_id)
        REFERENCES medicos(id),
    CONSTRAINT fk_me_especialidade FOREIGN KEY (especialidade_id)
        REFERENCES especialidades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABELA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    email     VARCHAR(150) NOT NULL UNIQUE,
    senha     VARCHAR(255) NOT NULL,   -- hash BCrypt!
    role      VARCHAR(20)  NOT NULL,   -- ADMIN, MEDICO, RECEPCIONISTA
    medico_id BIGINT,
    ativo     TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_usuario_medico FOREIGN KEY (medico_id)
        REFERENCES medicos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABELA: consultas
-- ============================================================
CREATE TABLE IF NOT EXISTS consultas (
    id                   BIGINT      NOT NULL AUTO_INCREMENT,
    paciente_id          BIGINT      NOT NULL,
    medico_id            BIGINT      NOT NULL,
    data_hora            DATETIME    NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    motivo_cancelamento  VARCHAR(255),
    criado_em            DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_consulta_paciente FOREIGN KEY (paciente_id)
        REFERENCES pacientes(id),
    CONSTRAINT fk_consulta_medico   FOREIGN KEY (medico_id)
        REFERENCES medicos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_consultas_data   ON consultas(data_hora);
CREATE INDEX idx_consultas_medico ON consultas(medico_id, data_hora);

-- ============================================================
-- TABELA: prontuarios (1:1 com consulta)
-- ============================================================
CREATE TABLE IF NOT EXISTS prontuarios (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    consulta_id BIGINT   NOT NULL UNIQUE,   -- UNIQUE garante 1:1
    anamnese    TEXT,
    diagnostico TEXT,
    prescricao  TEXT,
    observacoes TEXT,
    criado_em   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_prontuario_consulta FOREIGN KEY (consulta_id)
        REFERENCES consultas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


