# Links

## Overview

This project is a backend application that serves as an advanced link management system, extending the functionality of traditional link shorteners. Instead of shortening a single URL, each entry in the database corresponds to a unique code (e.g., `33333`) that maps to **four different links**, effectively providing four "slots" per code. This allows for greater flexibility in organizing and accessing grouped links under a single identifier.

## Features

- **Multi-Link Codes**: Associate up to four different URLs with a single short code for grouped link management.
- **Asynchronous and Non-Blocking Operations**: Leveraging Kotlin coroutines and suspend functions to ensure high performance and scalability.
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

- **Asynchronous Request Handling**: All HTTP requests are processed using non-blocking I/O, improving throughput and resource utilization.
- **Suspend Functions**: Core operations are implemented with Kotlin's `suspend` functions to manage concurrency efficiently.
- **Reactive Database Access**: Interaction with MongoDB is performed asynchronously, avoiding blocking threads and enhancing performance under load.

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
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" }
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
{ "payload": "https://github.com/", "action": "TAB" }
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
    "roles": ["edit", "admin"],
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
{ "username": "user", "password": "pass" }

//Response headers
Set-Cookie token=???
Set-Cookie refreshToken=???
```
- If both the `refreshToken` cookie and credentials are provided in the request, authentication will be performed using the supplied credentials.

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

- If both the `refreshToken` cookie and credentials are provided in the request, authentication will be performed using the supplied credentials.

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
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" }
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
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" }
    ]
}
```

### Get address - `GET` /api/addresss/`{id}`
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
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" }
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
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" },
        { "payload": "", "action": "TAB" }
    ]
}
```

### Delete address - `DELETE` /api/addresss/`{id}`
| Code  | Possible reason          |
|-------|--------------------------|
| 200   | OK                       |
| 400   | Bad request              |
| 400   | Invalid code             |
| 401   | Invalid or missing token |

```json
//Request headers
Cookie: token=???
```