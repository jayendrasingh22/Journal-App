# Security configuration

## Required JWT secret

The JWT signing key is no longer stored in source code. Set `JWT_SECRET` in the environment before using login or issuing tokens. Use a cryptographically random value of at least 32 characters; do not reuse the previously committed key.

PowerShell example (generate a fresh secret locally; do not commit it):

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
```

Rotating the key invalidates previously issued JWTs. Users must sign in again.

## Google OAuth 2.0

Configure the following environment variables:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

Register this authorized redirect URI in Google Cloud Console:

```
http://localhost:8080/login/oauth2/code/google
```

Start the authorization flow at:

```
http://localhost:8080/oauth2/authorization/google
```

After Google authenticates the user, the application checks that Google reports a verified email and matches it to an existing local account. It does not silently create an account or link an arbitrary Google identity. The callback responds with JSON containing an application JWT; store and transmit that token securely. For a browser application, prefer a backend-for-frontend or secure, HttpOnly cookie design rather than placing tokens in URLs or browser storage.

## API documentation

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

Use the Authorize control with a JWT in the form `Bearer <token>`.

## Important

The Google OAuth client registration uses placeholder values when environment variables are absent so that local development can start. Google sign-in will not work until real credentials and the matching redirect URI are configured. Never commit credentials or signing keys.
