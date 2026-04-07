# Return Photos + AI (workflow)

## Scop
Să avem o evidență foto la predare (baseline) și la retur, apoi un serviciu AI să compare imaginile și să marcheze automat cazurile ce necesită verificare.

## Flux (renter/client)
1. Intră la `Închiriere -> Poze de predare` (`/rentals/:id/start-photos`) și încarcă imagini baseline.
2. Înainte de retur, intră la `Poze înainte de retur` (`/rentals/:id/return-photos`), încarcă **minim 3** imagini.
3. Apasă `Trimite solicitare retur`.\n
Rezultat:\n
- `returnRequested = true`\n
- inventarul este marcat `PENDING_RETURN`.\n

## Flux (SuperOwner)
1. În `Admin -> Gestionare închirieri`, vede rândurile cu `returnRequested`.\n
2. Apasă `Rulează AI`.\n
3. Dacă scorul trece pragul, închirierea devine `flaggedForReview = true`.\n
4. Apasă `Decizie retur` și selectează `GOOD` sau `DAMAGED` + opțional `markCompleted`.\n

## Prag AI (auto-flag)
În backend există `app.ai.review-threshold` (default `0.4`).\n
În Docker, setează:\n
- `APP_AI_REVIEW_THRESHOLD=0.4` (exemplu)\n

