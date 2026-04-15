# How to run the app (Windows)

Acest ghid acoperă 2 variante:

- **Docker (recomandat)**: pornești tot stack-ul (frontend + backend + ngrok).
- **Local (fără Docker)**: pornești frontend și backend separat (util pentru debug în IntelliJ).

## 1) Rulare cu Docker (recomandat)

### Cerințe
- **Docker Desktop** instalat și pornit (WSL2 backend).
- Porturi libere: **3001** (frontend), **8081** (backend), **4040** (ngrok UI).

### Pași
1. Deschide PowerShell în:
   - `Licenta_Online_Renting/`
2. Configurează variabilele de mediu:
   - Copiază `docker.env.example` → `.env` (sau completează `.env` dacă există deja).
3. Pornește:

```bash
docker compose up -d --build
```

### URL-uri utile
- **Frontend (UI)**: `http://localhost:3001`
- **Backend (API)**: `http://localhost:8081/api/v1`
- **Ngrok dashboard**: `http://localhost:4040`

### Oprire / reset

```bash
docker compose down
```

Dacă vrei să cureți și volumele (atenție: șterge datele DB dacă folosești `local-db`):

```bash
docker compose down -v
```

## 2) Rulare local (fără Docker)

### 2.1 Frontend (Vite)

1. Intră în:
   - `Licenta_Online_Renting/frontend`
2. Instalează dependențe:

```bash
npm install
```

3. Pornește dev server:

```bash
npm run dev
```

Frontend-ul pornește (de obicei) pe `http://localhost:3001`.

### 2.2 Backend (Spring Boot / IntelliJ sau Maven)

#### Cerințe
- **Java 17**
- **Maven**

Verificare:

```bash
java -version
mvn -v
```

#### Pornire cu Maven (din terminal)
1. Intră în:
   - `Licenta_Online_Renting/online-rental-system-licenta`
2. Rulează:

```bash
mvn spring-boot:run
```

Backend-ul rulează pe `http://localhost:8081`.

#### Pornire din IntelliJ
- Importă proiectul Maven din `online-rental-system-licenta`
- Rulează `OnlineRentalApplication`
- Setează variabilele de mediu ca în `application.yml` (ex: `SPRING_DATASOURCE_URL`, `JWT_SECRET`, etc.)

## 3) Variabile importante (pe scurt)

Backend folosește variabile din `.env` / environment:

- **DB**:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- **JWT**:
  - `JWT_SECRET` (minim 32 caractere)
- **CORS**:
  - `APP_FRONTEND_ORIGIN` (ex: `http://localhost:3000,http://localhost:3001`)
- **Email (Gmail SMTP)**:
  - `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`
  - `APP_MAIL_FROM`
- **AI**:
  - `APP_AI_COMPARE_URL`
  - `APP_AI_READ_TIMEOUT_MS`
  - `APP_AI_IMAGE_BASE_URL` (dacă AI are nevoie de URL absolut către imagini)
- **Ngrok**:
  - `NGROK_AUTHTOKEN`

## 4) Troubleshooting (cele mai comune erori)

### Docker: “The system cannot find the file specified … dockerDesktopLinuxEngine”
- Docker Desktop nu rulează sau WSL2 backend nu e pornit.
- Soluție: pornește Docker Desktop și așteaptă până e “Running”, apoi reîncearcă `docker compose up -d --build`.

### `mvn : The term 'mvn' is not recognized`
- Maven nu e instalat sau nu e în PATH.
- Soluție (rapid, Winget):

```bash
winget install -e --id Apache.Maven
```

Închide/re-deschide terminalul și verifică `mvn -v`.

### 403 la `POST /products/{id}/reviews`
- Endpoint-ul de scriere recenzie cere autentificare; trebuie să trimiți `Authorization: Bearer <token>`.
- Dacă rulezi UI prin nginx (Docker), API-ul trebuie să forward-eze header-ul `Authorization` către backend.

