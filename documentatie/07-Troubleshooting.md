# Troubleshooting

## Frontend nu se actualizează
- fă refresh hard: `Ctrl+F5`
- reconstruiește: `docker compose up -d --build`

## Backend pornește greu
La pornire, Flyway rulează migrări și poate dura ~20-30 secunde (mai ales pe Neon).

## Login nu merge
Verifică `.env`:
- `APP_EMAIL_CONFIRMATION_REQUIRED=false` pentru dev (altfel trebuie confirmare email).

## Upload imagini nu merge
- backend are limită de 10MB/req.
- tipuri acceptate: jpg/jpeg/png/gif/webp.
- verifică dacă backend e UP și endpoint-ul `http://localhost:8081/images/...` răspunde.
- verifică volumul Docker `rental_uploads` (acolo sunt fișierele fizice).

## AI nu rulează
Setează `APP_AI_COMPARE_URL`.
Pentru AI în container separat, setează și `APP_AI_IMAGE_BASE_URL=http://host.docker.internal:8081`.
Dacă lipsește, AI apare ca `SKIPPED` și nu blochează fluxul.

