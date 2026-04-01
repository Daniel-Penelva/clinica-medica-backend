-- V3__insert_admin.sql
-- Local: src/main/resources/db/migration/
-- IMPORTANTE: gere seu proprio hash com BCryptPasswordEncoder(10)

INSERT INTO
    usuarios (email, senha, role, ativo)
VALUES (
        'admin@clinica.com',
        '$2a$12$DYgloZcTddVK5bdBNdPxMed71S3t2ObAZnmahYsqgGFP7PLGPU5wS',
        'ADMIN',
        1
    );