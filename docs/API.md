# Journal App API

Base URL for local development: http://localhost:8080 (unless changed in configuration).

Protected endpoints require:

~~~http
Authorization: Bearer <JWT>
~~~

The JWT is returned by POST /public/login. The current token implementation sets a one-hour expiration.

## Public endpoints

| Method | Path | Purpose | Access |
|---|---|---|---|
| GET | /public/health-check | Health check; returns OK | Public |
| POST | /public/signup | Register a user | Public |
| POST | /public/login | Authenticate and return a JWT | Public |

### Sign up

~~~http
POST /public/signup
Content-Type: application/json
~~~

Example body (fields based on the User entity):

~~~json
{
  "userName": "jay",
  "email": "jay@example.com",
  "password": "replace-with-a-local-test-password"
}
~~~

The signup controller currently returns no response body. User passwords are encoded with BCrypt by the user service.

### Login

~~~http
POST /public/login
Content-Type: application/json
~~~

~~~json
{
  "userName": "jay",
  "password": "replace-with-a-local-test-password"
}
~~~

On successful authentication, the response body is the JWT string. Use it as a Bearer token for protected endpoints.

## Journal endpoints

All journal endpoints require authentication. Operations use the authenticated principal to scope journal entries to the logged-in user.

| Method | Path | Purpose |
|---|---|---|
| GET | /journal | Get the authenticated user's journal entries |
| POST | /journal | Create a journal entry |
| GET | /journal/id/{myId} | Get one entry by MongoDB ObjectId |
| PUT | /journal/id/{myId} | Update one entry |
| DELETE | /journal/id/{myId} | Delete one entry |

### Create journal entry

~~~http
POST /journal
Authorization: Bearer <JWT>
Content-Type: application/json
~~~

~~~json
{
  "title": "First entry",
  "content": "A sample journal entry."
}
~~~

The server sets the entry date when creating the entry. The entity also has a sentiment field; its accepted values are defined by the project's Sentiment enum.

### Read, update, and delete

Replace {myId} with the entry's MongoDB ObjectId.

Example update body:

~~~json
{
  "title": "Updated title",
  "content": "Updated journal content."
}
~~~

**Implementation note:** Review and test the current update controller before relying on partial updates. It currently assigns content twice, so the second assignment can overwrite the earlier null/empty-value guard.

## User endpoints

All user endpoints require authentication.

| Method | Path | Purpose |
|---|---|---|
| GET | /user | Return a greeting for the authenticated user, including weather data when available |
| PUT | /user | Update the authenticated user's username and password |
| DELETE | /user | Delete the authenticated user's account |

The greeting endpoint currently requests weather for the city configured in the controller (Mumbai).

## Admin endpoints

These endpoints require the ADMIN role.

| Method | Path | Purpose |
|---|---|---|
| GET | /admin/all-users | List users |
| POST | /admin/create-admin-user | Create a user with USER and ADMIN roles |
| GET | /admin/clear-app-cache | Reload the application configuration cache |

## Status codes

The controllers use standard HTTP response codes including:
- 200 OK for successful reads, login, and updates that return a body.
- 201 Created for journal entry creation.
- 204 No Content for successful deletes and user updates.
- 400 Bad Request for failed login.
- 404 Not Found when a requested user or journal entry is not found.
- 500 Internal Server Error for journal creation failures.

Actual responses depend on the controller branch and current application state.
