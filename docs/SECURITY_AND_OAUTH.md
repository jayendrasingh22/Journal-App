# Security configuration

## Required JWT secret

The JWT signing key is no longer stored in source code. Set `JWT_SECRET` in the environment before using login or issuing tokens. Use a cryptographically random value of at least 32 characters; do not reuse the previously committed key.

PowerShell example (generate a fresh secret locally; do not commit it):

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
```

Rotating the key invalidates previously issued JWTs. Users must sign in again.

## Google OAuth 2.0

Configure:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

Register this authorized redirect URI in Google Cloud Console:

```
http://localhost:8080/login/oauth2/code/google
```

Start authorization at:

```
http://localhost:8080/oauth2/authorization/google
```

Spring Security's OpenID Connect integration validates the provider's ID token. The application additionally requires Google's `email_verified` claim and stores the stable Google `sub` identifier for subsequent sign-ins.

**Account-linking safety:** If no local account uses the verified Google email, the application creates a new USER account associated with that Google subject. If a local account already uses the email, the callback refuses automatic linking. This prevents an unverified local email registration from being taken over by a Google login. A separate authenticated account-linking flow is not implemented yet.

The callback responds with JSON containing an application JWT. Store and transmit that token securely. For a browser application, prefer a backend-for-frontend or secure, HttpOnly cookie design rather than placing tokens in URLs or browser storage.

## API documentation

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

Use the Authorize control with a JWT in the form `Bearer <token>`.

## Important

The Google client registration uses placeholder values when environment variables are absent so the app can start locally. Google sign-in will not work until real credentials and the matching redirect URI are configured. Never commit credentials or signing keys.
