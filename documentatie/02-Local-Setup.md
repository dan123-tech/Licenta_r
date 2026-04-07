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

