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
    uf               CHAR(2),
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

