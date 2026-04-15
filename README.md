Link a Frontend
https://github.com/emihdz1209/oracle-java-bot-frontend

# Oracle Java Bot – Backend

Backend API for **Oracle Java Bot**, a cloud-native system designed to improve development team productivity and visibility through automated workflows and Telegram bot integration.

This service exposes REST APIs for managing users, tasks, projects, and other entities used by the chatbot and the web frontend.

The backend is built with **Spring Boot**, uses **Oracle Autonomous Database**, and runs in **Docker** for easy setup and portability.

---

# Architecture Overview

The backend follows a **Feature-Based Architecture** combined with a layered structure inside each feature.

Each feature contains its own:

- Controller (API endpoints)
- Service (business logic)
- Repository (database access)
- Model (entities)
- DTOs (data transfer objects)

Example structure:

com.oraclejavabot

config
exception

features
users
controller
service
repository
model
dto


This structure keeps each feature **self-contained**, making the system easier to maintain and extend.

---

# Technology Stack

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Oracle Autonomous Database (ATP)**
- **Docker**
- **Maven**

---

# Running the Backend

The backend is fully dockerized. Developers **do not need Java or Maven installed locally**.

## Requirements

- Docker
- Git

Verify Docker installation:

docker --version
docker compose version


---

# Setup

## 1. Clone the repository

---

## 2. Create environment variables

SERVER_PORT=8080
DB_URL=jdbc:oracle:thin:@team43_tp?TNS_ADMIN=/opt/oracle/wallet
DB_USER=XXXXX
DB_PASSWORD=XXXXXx
TELEGRAM_BOT_ENABLED=false
TELEGRAM_BOT_TOKEN=
TELEGRAM_BOT_NAME=oracle-java-bot
TELEGRAM_BOT_DEFAULT_PRIORITY_ID=2
TELEGRAM_BOT_DEFAULT_STATUS_ID=1
TELEGRAM_BOT_DEFAULT_DUE_DAYS=7

Notes:
- Keep TELEGRAM_BOT_ENABLED=false if you do not want to run the bot.
- Set TELEGRAM_BOT_ENABLED=true and provide TELEGRAM_BOT_TOKEN to enable long polling.


---

## 3. Add Oracle Wallet

The Oracle wallet must be placed inside the `wallet/` directory.

Example structure:
wallet/
cwallet.sso
ewallet.p12
sqlnet.ora
tnsnames.ora


This folder is ignored by Git because it contains sensitive credentials.

---

## 4. Start the backend

Run:
docker compose up --build


The backend will start at:
http://localhost:8080


---

# API Example

Create user:
POST /api/users


Example request:
{
"primerNombre": "John",
"apellido": "Doe",
"telefono": "123456789",
"email": "john.doe@example.com
",
"telegramId": "123456789"
}


Example response:
{
"message": "Usuario creado correctamente",
"email": "john.doe@example.com
",
"telegramId": "123456789"
}


---

# Project Structure
src/main/java/com/oraclejavabot

config/
Application configuration

exception/
Global exception handling

features/
Domain features

users/
controller/
service/
repository/
model/
dto/



---

# Feature Development Guide

To add a new feature (for example **tasks**), follow these steps:

### 1 Create feature folder
features/tasks


### 2 Add subfolders
controller
service
repository
model
dto


### 3 Create entity

Example:
TaskEntity


### 4 Create repository
TaskRepository extends JpaRepository


### 5 Implement service logic
TaskService


### 6 Expose API
TaskController


This structure ensures each feature remains **modular and maintainable**.

---

# Development Workflow

Start backend:
docker compose up --build


Stop backend:
docker compose down


View logs:
docker compose logs -f


Telegram bot core commands:
- /start
- /project
- /tasklist
- /addtask
- /hide

