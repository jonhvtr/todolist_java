# 📝 TodoList API

Uma API RESTful completa para gerenciamento de tarefas (TodoList desenvolvida com Spring Boot, incluindo sistema de
lembretes (reminders) e visualização por calendário.

## 🚀 Tecnologias

- Java 17+
- Spring Boot 3.x
- Spring Data JPA - Persistência de dados
- Spring Validation - Validação de dados
- MySQL - Banco de dados (ajuste conforme seu projeto)
- Lombok - Redução de boilerplate
- SLF4J + Logback - Sistema de logs
- Swagger/OpenAPI - Documentação da API (se estiver usando)

## ✨ Funcionalidades

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
- ⏰ Buscar lembretes que precisam ser enviados

### Recursos Avançados

- 📅 Visualização de tarefas por mês (calendário)
- 🔍 Busca de tarefas por título/conteúdo
- 📊 Filtro por status e prioridade
- ⚠️ Tratamento de erros com Problem Detail (RFC 9457)
- 📝 Sistema de logs estruturado

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- MySQL (ou seu banco de dados)
- Git

## 🔧 Instalação e Configuração

**1. Clone o repositório**

   ````shell
        git clone https://github.com/seu-usuario/todolist_java.git
        cd todolist-api
   ````

**2. Configure o banco de dados**

Crie um banco de dados PostgreSQL:

   ````sql
       CREATE DATABASE todolist_db;
   ````

**3. Configure as variáveis de ambiente**

Use variáveis de ambiente:

````shell
    DB_HOST=localhost
    DB_PORT=3306
    DB_NAME=seu_database
    DB_USERNAME=seu_usuario
    DB_PASSWORD=sua_senha
````

**4. Rode a aplicação**

`````shell
   mvn spring-boot:run
``````

#### A API estará disponível em: http://localhost:8080

### 📚 Documentação da API
**Base URL**

````
http://localhost:8080/swagger-ui/index.html#/
````

## 📋 Endpoints da API

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
| GET     | `/tasks/reminder/pending`        | Lista lembretes pendentes            |
| GET     | `/tasks/reminder/to-send-now`    | Lembretes que devem ser enviados agora |

---

### 🔍 Busca e Filtros

| Método | Endpoint                                       | Descrição                                     |
|---------|------------------------------------------------|-----------------------------------------------|
| GET     | `/tasks/search?q={termo}`                      | Busca tarefas por título/conteúdo             |
| GET     | `/tasks/status-priority?status={status}&priority={priority}` | Filtra por status e/ou prioridade |
| GET     | `/tasks/calendar?year={year}&month={month}`    | Lista tarefas de um mês específico            |

## Exemplos de Requisições
### Criar uma tarefa
`````shell
    POST /tasks
    Content-Type: application/json
    
    {
    "title": "Estudar Spring Boot",
    "content": "Revisar conceitos de JPA e validações",
    "dueDate": "2025-10-30T18:00:00",
    "status": "PENDING",
    "priority": "HIGH"
    }
`````
### Adicionar lembrete
````shell
    PATCH /tasks/1/add-reminder
    Content-Type: application/json
    
    {
    "reminderDateTime": "2025-10-28T10:00:00"
    }
````
### Buscar tarefas do mês
````shell
    GET /tasks/calendar?month=10&year=2025
````
### Respostas de Erro (Problem Detail)
**A API usa o padrão RFC 9457 para erros:**
````shell

{
	"type": "https://api.todolist.com/errors/validation-error",
	"title": "Erro de validação nos parâmetros",
	"status": 400,
	"detail": "Um ou mais parâmetros da requisição são inválidos.",
	"instance": "/tasks/calendar",
	"app:errorCode": "VAL-400",
	"timestamp": "2025-10-23T19:52:31.580948600Z",
	"path": "/tasks/calendar",
	"traceId": "081ffb03-863b-488d-9c85-68a3c8ed0ac5",
	"invalidParams": {
		"getTasksByMonth.month": "deve estar entre 1 e 12"
	}
}

````

### 🗄️ Modelo de Dados
````shell
    Task
    java{
    "id": 1,
    "title": "String",
    "content": "String",
    "dueDate": "LocalDateTime",
    "status": "PENDING | IN_PROGRESS | COMPLETED",
    "priority": "NONE | LOW | MEDIUM | HIGH",
    "reminderDateTime": "LocalDateTime (nullable)",
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

## 📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
## ✍️ Autor
João Victor (jonhvtr)
LinkedIn: www.linkedin.com/in/jonhvtr-dev
