# Online Rental System (2026)

## Ce este
Aplicație web pentru închirierea de produse, cu:
- catalog public cu filtrare
- conturi și roluri (Client / Vendor / Admin / SuperOwner)
- rezervare + plată (Stripe)
- administrare produse și închirieri (SuperOwner)
- flux foto pentru predare/retur + comparare AI (manual trigger)

## Tehnologii
- **Frontend**: React + TypeScript + Vite
- **Backend**: Spring Boot 3 (Java 17), Spring Security (JWT), JPA/Hibernate, Flyway
- **DB**: PostgreSQL (local sau Neon)
- **Deploy local**: Docker Compose (nginx pentru frontend)

## URL-uri locale (Docker)
- Frontend: `http://localhost:3001`
- Backend: `http://localhost:8081/api/v1`

