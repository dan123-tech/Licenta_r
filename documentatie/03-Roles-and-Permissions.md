# Roluri și permisiuni

## Client (ROLE_CLIENT)
- vede catalogul și detaliile produselor
- creează închirieri și plătește
- vede `Închirierile mele`
- încarcă **poze de predare** (baseline) la începutul închirierii
- încarcă **poze înainte de retur** și trimite solicitare de retur

## Vendor (ROLE_VENDOR)
- poate gestiona produse în catalog (dacă ai activat asta în UI/route guards)
- NU are acces la administrarea completă a închirierilor

## Admin catalog (ROLE_ADMIN)
- gestionează produse în catalog
- NU are acces la închirieri și utilizatori (decât dacă este și SuperOwner)

## SuperOwner (ROLE_SUPEROWNER)
- acces complet în Admin:
  - dashboard
  - gestionare produse
  - gestionare închirieri (status, AWB, return decision)
  - statistici financiare
- rulează manual AI pentru comparația pozelor (baseline vs retur)
- poate marca o închiriere `RETURNED` / `COMPLETED` și decide `GOOD`/`DAMAGED`

