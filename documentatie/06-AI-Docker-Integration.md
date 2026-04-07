# Integrare AI Docker (comparare imagini)

Backend-ul are un adaptor `AiComparisonService` configurat via env.

## Variabile de mediu
Setează în `docker compose` / `.env`:
- `APP_AI_COMPARE_URL` (obligatoriu pentru a rula AI)
- `APP_AI_CONNECT_TIMEOUT_MS` (default 4000)
- `APP_AI_READ_TIMEOUT_MS` (default 12000)
- `APP_AI_REVIEW_THRESHOLD` (default 0.4)

## Contract (curent)
Request JSON trimis către AI:

```json
{
  "rentalId": 123,
  "baselineImageUrls": ["/images/...", "..."],
  "returnImageUrls": ["/images/...", "..."]
}
```

Response (flexibil): adaptorul încearcă să citească:
- `score` (sau `damageScore` / `similarityScore`)
- `predictedCondition` (sau `condition` / `verdict`)
- `needsReview` (sau `flagged`)
- `message` (sau `detail` / `statusMessage`)

Orice răspuns este salvat și ca `raw_response` pentru audit/debug.

## Notă despre URL-uri imagini
În DB se salvează URL-uri de forma `/images/<file>`.
Dacă serviciul AI rulează în alt container, poate avea nevoie de URL absolut (ex: `http://rental-backend:8081/images/...`).
\n
Dacă AI-ul tău cere URL absolut, îți ajustez adaptorul să prefixeze automat cu host-ul backend-ului.

