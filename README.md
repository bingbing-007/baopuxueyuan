# Baopu Xueyuan

Enterprise learning platform MVP with a deployable Spring Boot API, Vue web portal, MySQL seed data, and Docker Compose orchestration.

## Features

- Demo learner login
- Course catalog, enrollment, and learning progress updates
- Personal dashboard with enrolled courses, completed courses, and average progress
- MySQL persistence for users, courses, enrollments, and progress
- Nginx frontend hosting with `/api` reverse proxy

## Stack

- Java 21 + Spring Boot 3
- Vue 3 + TypeScript + Vite
- MySQL 8 / Redis / RabbitMQ
- Nginx + Docker Compose

## Deploy

1. Copy environment variables:

   ```bash
   cp .env.example .env
   ```

2. Update `.env` for production passwords, `WEB_PORT`, and CORS origins.

3. Build and start the stack:

   ```bash
   docker compose --env-file .env -f deploy/docker-compose.yml up -d --build
   ```

4. Open the web portal:

   ```text
   http://SERVER_IP:8081
   ```

Backend health check:

```text
http://SERVER_IP:8080/actuator/health
```

## Local Development

Backend:

```bash
cd backend
mvn -pl baopu-learning-service -am spring-boot:run
```

Frontend:

```bash
cd frontend/web-portal
npm install
npm run dev
```
