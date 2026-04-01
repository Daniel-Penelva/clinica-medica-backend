# Clinica Medica - Notas de Desenvolvimento

Para testar o login vai ser preciso pelo menos um usuário no banco. 

--- 

## COMO GERAR O HASH BCRYPT

### **Opção 1** - Site online: https://bcrypt-generator.com (rounds = 10)

### **Opção 2** - Snippet Java (rode uma vez e descarte):
    System.out.println(new BCryptPasswordEncoder(10).encode("sua_senha"));

    Copie o resultado e cole no SQL abaixo.

**DICA** — Snippet Java para gerar o hash (use e descarte):
    
    Crie um main temporário no projeto para gerar o hash:

```java
    public static void main(String[] args) {
    var encoder = new BCryptPasswordEncoder(10);
    System.out.println(encoder.encode("sua_senha"));
    // Copie o resultado para o V3__insert_admin.sql
    // Depois APAGUE este método
}
```

---

## Copie o HASH para o V3__insert_admin.sql

**ATENÇÃO!** gere seu próprio hash com BCryptPasswordEncoder(10)

**LOCAL:** src/main/resources/db/migration/

```sql
-- IMPORTANTE: Este é um exemplo de como pode ser utilizado.

INSERT INTO
    usuarios (email, senha, role, ativo)
VALUES (
        'admin@clinicamedica.com',
        '$2a$12$DYgloZcTddVK5bdBNdPxMed71S3t2ObAZnmahYsqgGFP7PLGPU5wS',
        'ADMIN',
        1
    );
```

---

## Como rodar o projeto - Testando com Swagger

### PASSO 1 - Suba a aplicacao
    - Log esperado:
    - Flyway - Successfully applied 1 migration: V3 - insert admin
    - Started ClinicaApplication in X seconds

### PASSO 2 - Abra o Swagger
    - http://localhost:8080/swagger-ui.html

### PASSO 3 - Faca o login
    - POST /api/v1/auth/login
    - Body: { "email": "seu_email", "senha": "sua_senha" }
    - Resposta: { "token": "eyJ...", "role": "ADMIN", "email": "seu_email" }

### PASSO 4 - Autorize o Swagger com o token
    - Clique no botao Authorize (cadeado) no topo do Swagger
    - Cola o token: eyJ... (o token completo que recebeu - OBS. Já vem escrito JWT (Bearer) no Swagger)
    - Clique em Authorize

### PASSO 5 - Teste um endpoint protegido
    - GET /api/v1/teste/ping (http://localhost:8080/api/v1/teste/ping)
    - Deve retornar 403 Forbidden — autenticado mas sem permissão de acesso ao recurso

### PASSO 6 - Teste sem token (logout)
    - Clique em Authorize e depois Logout
    - GET /api/v1/teste/ping -> deve retornar 401 Unauthorized

---
## Autor
**Daniel Penelva de Andrade**