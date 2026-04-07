# Troubleshooting

## Frontend nu se actualizează
- fă refresh hard: `Ctrl+F5`
- reconstruiește: `docker compose up -d --build`

## Backend pornește greu
La pornire, Flyway rulează migrări și poate dura ~20-30 secunde (mai ales pe Neon).\n

## Login nu merge
Verifică `.env`:\n
- `APP_EMAIL_CONFIRMATION_REQUIRED=false` pentru dev (altfel trebuie confirmare email).

## Upload imagini nu merge
- backend are limită de 10MB/req.\n
- tipuri acceptate: jpg/jpeg/png/gif/webp.\n

## AI nu rulează
Setează `APP_AI_COMPARE_URL`.\n
Dacă lipsește, AI apare ca `SKIPPED` și nu blochează fluxul.

