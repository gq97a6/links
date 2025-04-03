# Links

## Overview

This project is a backend application that serves as an advanced link management system, extending the functionality of
traditional link shorteners. Instead of shortening a single URL, each entry in the database corresponds to a unique
code (e.g., `33333`) that maps to **multiple different links**, effectively providing multiple "slots" per code. This
allows for greater flexibility in organizing and accessing grouped links under a single identifier.

## Features

- **Multi-Link Codes**: Associate multiple URLs with a single short code for grouped link management.
- **Asynchronous and Non-Blocking Operations**: Leveraging Kotlin coroutines and suspend functions to ensure high
  performance and scalability.
- **Database Integration**: Utilizes MongoDB for flexible and scalable data storage.
- **Containerized Deployment**: Deployable via Docker for consistency across environments.

## Technology Stack

- **Kotlin**: Primary programming language used for its modern features and interoperability with Java.
- **Quarkus**: Supersonic Subatomic Java framework tailored for Kubernetes and GraalVM integrations.
- **Kotlin Coroutines**: Enables asynchronous programming with a simple and concise codebase.
- **Jackson**: Facilitates JSON parsing and serialization for seamless data interchange.
- **MongoDB**: NoSQL database chosen for its scalability and flexibility in handling diverse data structures.
- **Docker**: Ensures the application can be easily deployed and run in isolated environments.

## Architecture

The application is designed with a focus on asynchronicity and non-blocking operations:

- **Asynchronous Request Handling**: All HTTP requests are processed using non-blocking I/O, improving throughput and
  resource utilization.
- **Suspend Functions**: Core operations are implemented with Kotlin's `suspend` functions to manage concurrency
  efficiently.
- **Reactive Database Access**: Interaction with MongoDB is performed asynchronously, avoiding blocking threads and
  enhancing performance under load.

<br>
<br>

# Example Docker Compose

```yml
name: links

services:
  links.backend:
    container_name: links.backend
    hostname: links.backend
    image: gq97a6/links
    restart: unless-stopped
    environment:
      SERVER_DOMAIN: localhost
      SERVER_ORIGIN: https://localhost
      QUARKUS_MONGODB_CONNECTION_STRING: mongodb://root:1234@mongodb:27017
      JWT_TOKEN_PRIMARY_EXPIRE: 300
      JWT_TOKEN_REFRESH_EXPIRE: 7200
      QUARKUS_HTTP_CORS_ENABLED: true
      
  links.mongodb:
    container_name: links.mongodb
    hostname: links.mongodb
    image: mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: 1234
    
  links.mongoexpress:
    container_name: links.mongoexpress
    hostname: links.mongoexpress
    image: mongo-express
    restart: always
    environment:
      ME_CONFIG_MONGODB_ADMINUSERNAME: root
      ME_CONFIG_MONGODB_ADMINPASSWORD: 1234
      ME_CONFIG_MONGODB_URL: mongodb://root:1234@links.mongodb:27017
      ME_CONFIG_BASICAUTH: false
```

# MongoDB Collections

| Collection | Example document                                                                                                                                                                                                         |
|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| address    | `{"_id":ObjectId(), "direct":false, "code":"12345", "links":[{"payload":"", "action":"TAB"}, {"payload":"", "action":"COPY"}, {"payload":"", "action":"TAB"}, {"payload":"", "action":"TAB"}], "lastChange":1742652283}` |
| user       | `{"_id":ObjectId(), "name":"user", "pass":"$2a$12$V5l5E58oo1vDXLk0YhUan.dDk2wfnzhpkOXsN/iqfSoNNJ4N7HGvC", "roles":["edit"], "lastChange":1725311805}`                                                                    |

# Configuration Options

Configuration can be done using Docker environment variables. The most commonly used ones are listed below.  
Additional configuration options are available: https://quarkus.io/guides/all-config

**Note:** When converting Quarkus-style property keys to environment variable format, use the following conventions:
- Replace dots and dashes with underscores
- Convert all letters to uppercase

**For example:**
- `jwt.sign.key.location` => `JWT_SIGN_KEY_LOCATION`
- `quarkus.http.ssl-port` => `QUARKUS_HTTP_SSL_PORT`

| Environment Variable                     | Description                                            | Default Value                       |
|------------------------------------------|--------------------------------------------------------|-------------------------------------|
| `SERVER_DOMAIN`                          | Server domain                                          | localhost                           |
| `SERVER_ORIGIN`                          | Server origin                                          | http://localhost                    |
| `JWT_SIGN_KEY_LOCATION`                  | Path to key used to `sign` JWTs                        | certs/jwt/privateKey.pem            |
| `JWT_VERIFY_KEY_LOCATION`                | Path to key used to `verify` the signature of JWTs     | certs/jwt/publicKey.pem             |
| `JWT_ENCRYPT_KEY_LOCATION`               | Path to key used to `encrypt` the contents of the JWTs | certs/jwt/publicKey.pem             |
| `JWT_DECRYPT_KEY_LOCATION`               | Path to key used to `decrypt` encrypted JWTs           | certs/jwt/privateKey.pem            |
| `JWT_TOKEN_PRIMARY_EXPIRE`               | Primary JWT expiration time in ms                      | 300                                 |
| `JWT_TOKEN_REFRESH_EXPIRE`               | Refresh JWT expiration time in ms                      | 7200                                |
| `QUARKUS_MONGODB_CONNECTION_STRING`      | Connection string to database                          | mongodb://root:1234@localhost:27017 |
| `QUARKUS_MONGODB_DATABASE`               | Database name                                          | link                                |
| `QUARKUS_HTTP_SSL_CERTIFICATE_FILES`     | Path to SSL certificate                                | certs/ssl/fullchain.pem             |
| `QUARKUS_HTTP_SSL_CERTIFICATE_KEY_FILES` | Path to SSL key                                        | certs/ssl/privkey.pem               |
| `QUARKUS_HTTP_SSL_PORT`                  | HTTPS port                                             | 443                                 |
| `QUARKUS_HTTP_PORT`                      | HTTP port                                              | 80                                  |
| `QUARKUS_HTTP_HOST`                      | HTTP host                                              | 0.0.0.0                             |
| `QUARKUS_HTTP_INSECURE_REQUESTS`         | If HTTP is enabled                                     | enabled                             |
| `QUARKUS_HTTP_CORS_ENABLED`              | If CORS is enabled                                     | false                               |

<br>
<br>

# API Documentation

## Classes

### Address

| Field    | Datatype     | Description                           |
|----------|--------------|---------------------------------------|
| `links`  | Array\<Link> | List of four links under this address |
| `id`     | String       | UUID of this address                  |
| `direct` | Boolean      | If link is direct                     |
| `code`   | String       | Access code of this address           |

Details:

- `code` - only uppercase alphanumeric
- `direct` - direct address redirects staight to first link

Example:

```json
{
  "id": "67c98afee6f4db5c6a17040a",
  "direct": false,
  "code": "1234",
  "links": [
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    }
  ]
}
```

<br>

### Link

| Field     | Datatype | Description         |
|-----------|----------|---------------------|
| `payload` | String   | URL or text         |
| `action`  | String   | What this link does |

Details:

- `action` - can be one of following:
    - `TAB`- opens link in a new tab
    - `LINK` - opens link in the current tab
    - `COPY` - copies payload to the clipboard

Example:

```json
{
  "payload": "https://github.com/",
  "action": "TAB"
}
```

<br>

### User

| Field        | Datatype       | Description                              |
|--------------|----------------|------------------------------------------|
| `id`         | String         | UUID of this user                        |
| `name`       | String         | Username of this user                    |
| `pass`       | String         | Bcrypt hashed password                   |
| `roles`      | Array\<String> | List of permission of this user          |
| `lastChange` | Number         | Epoch time in seconds of last alteration |

Example:

```json
{
  "id": "67c98afee6f4db5c6a17040a",
  "name": "user",
  "pass": "$2a$12$5mMUvHEwNMEEHt0oqxJIWOgVzRWWsv/UY.MLaLUBPOPWcXXetcvP2",
  "roles": [
    "edit",
    "admin"
  ],
  "lastChange": 0
}
```

<br>

## Endpoints

- JWT is to be stored in cookie named `token`.
- Refresh token is to be stored in cookie named `refreshToken`.

### Health check - `GET` /api/test

| Code  | Possible reason    |
|-------|--------------------|
| 200   | OK                 |
| Other | Something is wrong |

### Login with credentials - `POST` /api/login

| Code | Possible reason                     |
|------|-------------------------------------|
| 200  | OK                                  |
| 401  | Invalid password                    |
| 401  | This user does not exist            |
| 400  | Missing both token and credentials  |
| 400  | Blank password or username supplied |

```json
//Request body
{
  "username": "user",
  "password": "pass"
}

//Response headers
Set-Cookie token=???
Set-Cookie refreshToken=???
```

- If both the `refreshToken` cookie and credentials are provided in the request, authentication will be performed using
  the supplied credentials.

### Login with JWT - `POST` /api/login

| Code | Possible reason                                |
|------|------------------------------------------------|
| 200  | OK                                             |
| 400  | Invalid token                                  |
| 400  | Missing both refresh token and credentials     |
| 401  | This user does not exist                       |
| 401  | User record altered since refresh token issued |

```json
//Request headers
Cookie: refreshToken=???

//Response headers
Set-Cookie token=???
Set-Cookie refreshToken=???
```

- If both the `refreshToken` cookie and credentials are provided in the request, authentication will be performed using
  the supplied credentials.

### Create new address - `POST` /api/address

| Code | Possible reason                       |
|------|---------------------------------------|
| 200  | OK                                    |
| 400  | Bad request                           |
| 400  | Address with that code already exists |
| 400  | Invalid address (eg. invalid code)    |
| 401  | Invalid or missing token              |

```json
//Request headers
Cookie: token=???

//Request body - Address object
{
"direct": false,
"code": "???",
"links": [
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"}
]
}

//Field "id" is to be omitted
//Field "code" can be omitted, it will be assigned server-side
//Size of list links must be equal 4

//Response body
{
"id": "???",
"direct": false,
"code": "???",
"links": [
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"}
]
}
```

### Get address - `GET` /api/addresss/`{code}`

| Code | Possible reason                        |
|------|----------------------------------------|
| 200  | OK                                     |
| 400  | Invalid code supplied                  |
| 404  | Address with that code does not exists |

```json
//Response body
{
  "id": "???",
  "direct": false,
  "code": "???",
  "links": [
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    },
    {
      "payload": "",
      "action": "TAB"
    }
  ]
}
```

### Edit address - `PUT` /api/addresss/`{code}`

| Code | Possible reason                                 |
|------|-------------------------------------------------|
| 200  | OK                                              |
| 400  | Bad request                                     |
| 400  | Address is missing valid id                     |
| 400  | Code in address and code in path does not match |
| 401  | Invalid or missing token                        |
| 404  | Address with that code does not exist           |
| 400  | Invalid address (eg. invalid code)              |

```json
//Request headers
Cookie: token=???

//Request body
{
"id": "???",
"direct": false,
"code": "???",
"links": [
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"},
{"payload": "", "action": "TAB"}
]
}
```

### Delete address - `DELETE` /api/addresss/`{code}`

| Code | Possible reason          |
|------|--------------------------|
| 200  | OK                       |
| 400  | Bad request              |
| 400  | Invalid code             |
| 401  | Invalid or missing token |

```json
//Request headers
Cookie: token=???
```
