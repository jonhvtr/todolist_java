# 📝 TodoList API

Uma API RESTful completa para gerenciamento de tarefas (TodoList desenvolvida com Spring Boot, incluindo sistema de
lembretes (reminders) e visualização por calendário.

## 🚀 Tecnologias

- Java 25+
- Spring Boot 3.x
- Spring Data JPA - Persistência de dados
- Spring Validation - Validação de dados
- Spring Security - Camada de segurança da aplicação, controle de autenticação e autorização.
- OAuth2 / JWT - Autenticação baseada em tokens, suportando fluxos OAuth2 e segurança stateless.
- PostgreSQL - Banco de dados relacional utilizado pela aplicação.
- Liquibase - Controle de versionamento e migração de schema do banco de dados.
- MapStruct - Mapeamento eficiente entre DTOs e entidades.
- JUnit 5 - Framework para testes unitários.
- Mockito - Framework para criação de mocks em testes unitários e de integração.
- Docker - Containerização da aplicação e serviços dependentes. 
- Kubernetes - Orquestração de containers, gerenciamento de deploys e escalabilidade.
- Lombok - Redução de boilerplate
- SLF4J + Logback - Sistema de logs
- Swagger/OpenAPI - Documentação da API (se estiver usando)

## ✨ Funcionalidades

### Criar Perfil
- 🔒 Cadastro de usuários (registro)
- 🔒 Autenticação de usuários (login)

### Gerenciamento de Tarefas

- ✅ Criar, listar, atualizar e deletar tarefas
- ✅ Marcar tarefas como completas
- ✅ Definir prioridades (NONE, LOW, MEDIUM, HIGH)
- ✅ Definir status (PENDING, IN_PROGRESS, COMPLETED)
- ✅ Adicionar data de vencimento (due date)

### Sistema de Lembretes (Reminders)

- ⏰ Adicionar lembretes a qualquer tarefa
- ⏰ Remover lembretes
- ⏰ Listar lembretes pendentes

### Recursos Avançados

- 📅 Visualização de tarefas por mês (calendário)
- 🔍 Busca de tarefas por título
- 📊 Filtro por status e prioridade
- ⚠️ Tratamento de erros com Problem Detail (RFC 9457)
- 📝 Sistema de logs estruturado

## 📋 Pré-requisitos

- Java  25+
- Maven 3.9+
- Docker - Para execução do PostgreSQL e demais serviços em container
- Docker Compose (opcional, mas recomendado) - Para orquestração local dos containers
- Kubernetes (Kind ou Minikube) - Para execução do ambiente em cluster local
- kubectl - CLI para gerenciamento do cluster Kubernetes
- PostgreSQL (opcional local) - Necessário apenas se não utilizar Docker/Kubernetes
- Git

## 🔧 Instalação e Configuração

**1. Clone o repositório**

   ````shell
        git clone https://github.com/seu-usuario/todolist_java.git
        cd todolist-api
   ````

## 🐘 2. Configure o banco de dados

### Opção 1 — Docker (recomendado)

```yaml
environment:
  POSTGRES_DB: todolistdb
  POSTGRES_USER: dcs_user
  POSTGRES_PASSWORD: dcs_password
```

### Opção 2 - Banco local
```sql
    CREATE DATABASE todolistdb;
```

## ⚙️ 3. Variáveis de ambiente

A aplicação utiliza DB_URL como conexão principal.

#### Local
```shell
    DB_URL=jdbc:postgresql://localhost:5432/todolistdb
    DB_USERNAME=dcs_user
    DB_PASSWORD=dcs_password
```

#### Docker Compose
```shell
    DB_URL=jdbc:postgresql://dcs-postgres:5432/todolistdb
    DB_USERNAME=dcs_user
    DB_PASSWORD=dcs_password
```

#### Kubernetes
```shell
    DB_URL=jdbc:postgresql://postgres:5432/todolistdb
    DB_USERNAME=dcs_user
    DB_PASSWORD=dcs_password
```

## 🚀 4. Executando a aplicação

### Local (Maven)
```shell
    mvn spring-boot:run
```

### Docker Compose
```shell
    docker compose up -d
```

### Kubernetes
```shell
    kubectl apply -f k8s/
    kubectl rollout restart deployment todolist-api
```

#### A API estará disponível em: http://localhost:8080

## 📋 Endpoints da API

### Cliente (Client)

| Método | Endpoint         | Descrição       |
|--------|------------------|-----------------|
| POST   | `/auth/register` | Cria um usuário |
| POST   | `/auth/login`    | Realiza o login |

### 🗂️ Tarefas (Tasks)

| Método | Endpoint                 | Descrição                            |
|---------|--------------------------|--------------------------------------|
| GET     | `/tasks`                 | Lista todas as tarefas               |
| POST    | `/tasks`                 | Cria uma nova tarefa                 |
| GET     | `/tasks/{taskId}`        | Busca uma tarefa específica          |
| PUT     | `/tasks`                 | Atualiza uma tarefa completa         |
| DELETE  | `/tasks/{taskId}`        | Deleta uma tarefa                    |
| PATCH   | `/tasks/{taskId}/complete` | Marca tarefa como completa         |
| PATCH   | `/tasks/{taskId}/due-date` | Atualiza data de vencimento         |

---

### ⏰ Lembretes (Reminders)

| Método | Endpoint                         | Descrição                            |
|---------|----------------------------------|--------------------------------------|
| PATCH   | `/tasks/{taskId}/add-reminder`   | Adiciona lembrete a uma tarefa       |
| PATCH   | `/tasks/{taskId}/remove-reminder`| Remove lembrete de uma tarefa        |
| GET     | `/tasks/reminder`                | Lista todas as tarefas com lembretes |

---

### 🔍 Busca e Filtros

| Método | Endpoint                                      | Descrição                                     |
|---------|-----------------------------------------------|-----------------------------------------------|
| GET     | `/tasks/search?q={termo}`                     | Busca tarefas por título             |
| GET     | `/tasks/ | Filtra por status e/ou prioridade |
| GET     | `/tasks/calendar`    | Lista tarefas de um mês específico            |

## Exemplos de Requisições
### Criar uma tarefa
`````shell
    POST /tasks
    Content-Type: application/json
    
    {
        "title": "Task",
        "content": "Content",
        "dueDate": "dd/MM/yyyy HH:mm",
        "priority": "HIGH"
    }
`````
### Adicionar lembrete
````shell
    PATCH /tasks/{taskId}/add-reminder
    Content-Type: application/json
    
    {
      "dateTime": "dd/MM/yyyy HH:mm"
    }
````

### Respostas de Erro (Problem Detail)
**A API usa o padrão RFC 9457 para erros:**
````shell

{
  "type": "https://api.todolist.com/errors/validation",
  "title": "Invalid data",
  "status": 400,
  "detail": "invalid fields",
  "instance": "/tasks",
  "app:errorCode": "VLD-400",
  "timestamp": "2026-05-26T21:52:08.684226867",
  "errors": {
    "title": "Title is required."
  }
}
````

### 🗄️ Modelo de Dados
````shell
    Task
    {
      "id": "c501aa8e-93d0-4f8e-9807-090q8e6e72d6",
      "client": {
        "id": "c1fcc20c-ddb5-4388-a1e8-7126a674a783",
        "email": "User_nzc04e@test.com"
      },
      "title": "String",
      "content": "String",
      "dueDate": "LocalDateTime dd/MM/yyyy HH:mm",
      "status": "PENDING",
      "priority": "NONE | LOW | MEDIUM | HIGH",
      "reminderDateTime": LocalDateTime (nullable),
      "createdAt": "LocalDateTime",
      "updatedAt": "LocalDateTime"
    }
````

## 📝 Logs
````shell
    O sistema usa SLF4J para logs estruturados:
    2025-10-22 10:30:00 - INFO - Creating new task: Estudar Spring Boot
    2025-10-22 10:30:01 - INFO - Task created successfully with id: 1
    2025-10-22 10:35:00 - INFO - Adding reminder to task 1: 2025-10-28T10:00:00
````

### 📚 Documentação da API
**Base URL**

````
http://localhost:8080/swagger-ui/index.html#/
````

## 📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
## ✍️ Autor
João Victor (jonhvtr)
LinkedIn: www.linkedin.com/in/jonhvtr-dev
