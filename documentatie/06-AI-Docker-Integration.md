# Integrare AI Docker (comparare imagini)

Backend-ul are un adaptor `AiComparisonService` configurat via env.

## Variabile de mediu
Setează în `docker compose` / `.env`:
- `APP_AI_COMPARE_URL` (obligatoriu pentru a rula AI)
- `APP_AI_CONNECT_TIMEOUT_MS` (default 4000)
- `APP_AI_READ_TIMEOUT_MS` (default 12000)
- `APP_AI_REVIEW_THRESHOLD` (default 0.4)
- `APP_AI_IMAGE_BASE_URL` (opțional; util când AI rulează în alt container/host)

## Contract (curent)
Request JSON trimis către AI:

```json
{
  "rentalId": 123,
  "baselineImageUrls": ["http://host.docker.internal:8081/images/...", "..."],
  "returnImageUrls": ["http://host.docker.internal:8081/images/...", "..."]
}
```

Response (flexibil): adaptorul încearcă să citească:
- `score` (sau `damageScore` / `similarityScore`)
- `newDamageScore` (sau `new_damage_score` / `damage_delta`)
- `modelMatchScore` (sau `model_match_score`)
- `predictedCondition` (sau `condition` / `verdict`)
- `ocrText`, `powerOnDetected`, `errorCodesDetected`
- `detectedBrand`, `detectedModel`, `serialNumberDetected`
- `needsReview` (sau `flagged`)
- `message` (sau `detail` / `statusMessage`)

Orice răspuns este salvat și ca `raw_response` pentru audit/debug.

## Unde sunt stocate imaginile
- Fișierele uploadate sunt pe backend în `/app/uploads/images`.
- În DB se salvează URL-uri în:
  - `rental_baseline_images.image_url`
  - `rental_return_images.image_url`
- Pentru AI, backend normalizează URL-urile:
  - dacă URL-ul este relativ (`/images/...`) și `APP_AI_IMAGE_BASE_URL` este setat, trimite URL absolut către AI.

Exemplu recomandat în Docker Desktop (Windows):
- `APP_AI_COMPARE_URL=http://host.docker.internal:8001/analyze/esthetic-shape`
- `APP_AI_IMAGE_BASE_URL=http://host.docker.internal:8081`

