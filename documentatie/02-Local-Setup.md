# Setup local (Docker)

## 1) Config `.env`
În rădăcină (`Licenta_Online_Renting/.env`) setează variabilele necesare.
Poți porni fie cu Neon, fie cu DB locală.

Model: `docker.env.example`.

## 2) Pornire
Din `Licenta_Online_Renting/`:

```bash
docker compose up -d --build
```

## 3) DB local (opțional)

```bash
docker compose --profile local-db up -d --build
```

## 4) Oprire

```bash
docker compose down
```

## 5) Reset DB local (atenție: șterge datele)

```bash
docker compose --profile local-db down -v
```

## 6) Unde sunt stocate imaginile
- **În container (backend):** `/app/uploads/images`
- **Persistență Docker:** volumul `rental_uploads` (mapat pe `/app/uploads`)
- **Expunere HTTP:** backend servește fișierele prin `/images/**`
  - exemplu: `http://localhost:8081/images/<fisier>`
- **În baza de date:**
  - `products.image_url` pentru imaginile produselor
  - `rental_baseline_images.image_url` pentru poze de predare
  - `rental_return_images.image_url` pentru poze înainte de retur

Notă: pentru pozele încărcate de utilizator se salvează de regulă căi relative (`/images/...`), iar frontend le afișează folosind host-ul backend.

