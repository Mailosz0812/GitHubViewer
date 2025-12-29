# GitHubViewer Proxy API

GitHubViewer is a proxy service that interacts with the GitHub API to provide a simplified view of a user's repositories. It specifically filters out forked repositories and includes branch information for each repository based on the provided GitHub username.

## Tech Stack

### Implementation
- **Java 25**
- **Spring Boot 4.0.1**
- **Gradle (Kotlin DSL)**
- **RestClient** (modern, synchronous HTTP client)

### Testing
- **WebTestClient** (integration testing)
- **WireMock** (API mocking and emulation on port `8081`)

## Getting Started

### Prerequisites
- JDK 25

### Running the Application
The application runs on port `8080`. To start it, execute the following command in the root directory:
```bash
./gradlew bootRun
```

### Running Tests
The project includes comprehensive integration tests that verify business logic and error handling using WireMock to emulate the backing GitHub API (v3).

```bash
./gradlew clean test
```
### API Usage
The application exposes a single endpoint under a configured context path.

Endpoint: GET /GithubViewer/user/{username}

Example Request:

```bash
curl -X GET http://localhost:8080/GithubViewer/user/Mailosz0812 -H "Accept: application/json"
```
### Success Response (200 OK):

```JSON
[
  {
    "name": "DietPlanner",
    "owner": {
      "login": "Mailosz0812"
    },
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "594f0617b0a186ddb5b8ab1b80c48831f00c0a23"
      }
    ]
  }
]
```
## Design Decisions
**Architecture:** 
The application is structured in a Controller-Service-Client architecture to keep the implementation simple, clean, and easy to maintain.

**RestClient:**
Used instead of WebFlux's WebClient to keep the stack simple and compliant with requirements.

**Integration Testing:**
Instead of mocking internal classes, the project uses WireMock to emulate external API behavior, ensuring high-fidelity testing of the network and mapping layers.

