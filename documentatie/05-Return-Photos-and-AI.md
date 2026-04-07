# Return Photos + AI (workflow)

## Scop
Să avem o evidență foto la predare (baseline) și la retur, apoi un serviciu AI să compare imaginile și să marcheze automat cazurile ce necesită verificare.

## Flux (renter/client)
1. Intră la `Închiriere -> Poze de predare` (`/rentals/:id/start-photos`) și încarcă imagini baseline.
2. Înainte de retur, intră la `Poze înainte de retur` (`/rentals/:id/return-photos`), încarcă **minim 3** imagini.
3. Apasă `Trimite solicitare retur`.

Rezultat:
- `returnRequested = true`
- inventarul este marcat `PENDING_RETURN`.

## Flux (SuperOwner)
1. În `Admin -> Gestionare închirieri`, vede rândurile cu `returnRequested`.
2. Apasă `Rulează AI`.
3. Dacă scorul trece pragul, închirierea devine `flaggedForReview = true`.
4. Apasă `Decizie retur` și selectează `GOOD` sau `DAMAGED` + opțional `markCompleted`.

## Flux nou: verificare pe etape (wizard)
- Etapa `BASELINE`: după upload poze de predare se poate rula `run-handover-verification?stage=BASELINE`.
- Etapa `RETURN`: după upload poze retur se poate rula `run-handover-verification?stage=RETURN`.
- Rezultatul AI include:
  - `newDamageScore` (delta damage)
  - `modelMatchScore` + verdict (`MATCH`, `MISMATCH`, `REVIEW_REQUIRED`, `FAILED`)
  - `ocrText`, `powerOnDetected`, `errorCodesDetected`
- Auditul se salvează în `rental_photo_verifications`.

## Prag AI (auto-flag)
În backend există `app.ai.review-threshold` (default `0.4`).
În Docker, setează:
- `APP_AI_REVIEW_THRESHOLD=0.4` (exemplu)

## Stocare imagini pentru fluxul de retur
- Pozele de predare se salvează în tabela `rental_baseline_images`.
- Pozele de retur se salvează în tabela `rental_return_images`.
- Fișierele fizice sunt în `/app/uploads/images` (volum Docker: `rental_uploads`).

