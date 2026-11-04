# Journal App API

Base URL: `http://localhost:8080`.

## Authentication

Protected endpoints require:

~~~http
Authorization: Bearer <JWT>
~~~

### Register

~~~http
POST /public/signup
Content-Type: application/json

{
  "userName": "jay",
  "email": "jay@example.com",
  "password": "a-long-test-password"
}
~~~

The server assigns the `USER` role. Clients cannot submit roles, IDs, journal references, or password hashes. Passwords must be 12–72 characters.

### Login

~~~http
POST /public/login
Content-Type: application/json

{
  "userName": "jay",
  "password": "a-long-test-password"
}
~~~

Successful response:

~~~json
{
  "tokenType": "Bearer",
  "accessToken": "<JWT>",
  "userName": "jay"
}
~~~

### Google OAuth 2.0

Start at `GET /oauth2/authorization/google`. Google redirects to `/login/oauth2/code/google`. Spring Security validates the OpenID Connect identity token, and the application requires a verified email. A new Google subject creates a local USER account; an existing local account with the same email is not automatically linked. See [SECURITY_AND_OAUTH.md](SECURITY_AND_OAUTH.md).

## Journal endpoints

All journal endpoints require authentication and operate only on the authenticated user's entries.

| Method | Path | Purpose |
|---|---|---|
| GET | `/journal` | List current user's journal entries |
| POST | `/journal` | Create a journal entry |
| GET | `/journal/id/{myId}` | Read one owned entry |
| PUT | `/journal/id/{myId}` | Update one owned entry |
| DELETE | `/journal/id/{myId}` | Delete one owned entry |

Create example:

~~~http
POST /journal
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "title": "First entry",
  "content": "A sample journal entry."
}
~~~

The server sets the entry date. The client cannot set the entry ID, date, or sentiment in this request DTO.

Update example:

~~~http
PUT /journal/id/<mongo-object-id>
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "title": "Updated title",
  "content": "Updated journal content."
}
~~~

The request requires a nonblank title; content may be omitted to leave the existing content unchanged.

## User endpoints

All require authentication.

| Method | Path | Purpose |
|---|---|---|
| GET | `/user` | Greeting and weather information |
| PUT | `/user` | Update username and/or email |
| DELETE | `/user` | Delete authenticated user's account |

Profile update example:

~~~json
{
  "userName": "new-username",
  "email": "new@example.com"
}
~~~

Password and role fields are not accepted by this endpoint. A username change means the existing JWT subject is no longer the current username; sign in again to obtain a new token.

## Admin endpoints

Require the `ADMIN` role.

| Method | Path | Purpose |
|---|---|---|
| GET | `/admin/all-users` | List user response DTOs (no password field) |
| POST | `/admin/create-admin-user` | Create an admin account |
| GET | `/admin/clear-app-cache` | Reload application cache |

Admin creation body:

~~~json
{
  "userName": "admin-user",
  "email": "admin@example.com",
  "password": "a-long-test-password"
}
~~~

The server assigns the admin role; it is not accepted from the request body.

## Swagger and OpenAPI

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

Use the Swagger **Authorize** control with `Bearer <JWT>`.

## Error response format

Validation, authentication, and authorization failures use a consistent JSON shape:

~~~json
{
  "timestamp": "2026-11-04T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/public/signup",
  "fieldErrors": {
    "email": "must be a well-formed email address"
  }
}
~~~

The exact timestamp, message, and field errors depend on the request.
