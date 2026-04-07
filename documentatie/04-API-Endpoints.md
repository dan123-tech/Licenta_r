# API Endpoints (rezumat)

Prefix: `/api/v1`

## Auth
- `POST /auth/login`
- `POST /auth/register`
- `GET /auth/confirm?token=...`

## Products (public read)
- `GET /products`
- `GET /products/{id}`
- `GET /products/filters/...` (folosit de mega-menu)

## Rentals (client)
- `POST /rentals`
- `GET /rentals/my`
- `GET /rentals/{id}`

## Payments
- `POST /payments/create-intent`
- `POST /payments/confirm/{paymentIntentId}`

## Rentals (SuperOwner admin)
- `GET /rentals`
- `PUT /rentals/{id}/status?newStatus=...`
- `DELETE /rentals/{id}`
- `POST /rentals/{id}/generate-awb`

## Return Photos + AI (nou)
- `POST /rentals/{id}/baseline-photos` (multipart: `file`)
- `POST /rentals/{id}/return-photos` (multipart: `file`)
- `GET /rentals/{id}/return-workflow`
- `POST /rentals/{id}/submit-return-request`
- `POST /rentals/{id}/run-ai-comparison` (SuperOwner)
- `POST /rentals/{id}/review-return` (SuperOwner, body: `{ condition, notes?, markCompleted }`)

