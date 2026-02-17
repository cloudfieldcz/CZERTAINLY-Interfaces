# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ApiClientTest

# Run a single test method
mvn test -Dtest=ApiClientTest#testGetAttributes_unknownHost
```

## Project Overview

CZERTAINLY-Interfaces is a Java library providing API interfaces and data models for the CZERTAINLY certificate management platform. It defines contracts for communication between the platform core and connectors.

## Architecture

### Package Structure (`src/main/java/com/czertainly/api/`)

- **interfaces/** - Spring REST controller interfaces with OpenAPI annotations
  - `connector/` - Interfaces that connector implementations must implement (Attributes, Health, Info, Discovery, Authority, etc.)
  - `core/` - Platform core interfaces (ACME, SCEP, CMP protocols, web management APIs)
  - `client/` - Sync client interfaces for connector communication (REST or MQ-based)
  - Base interfaces: `AuthProtectedController`, `AuthProtectedConnectorController`, `NoAuthController` define security requirements

- **clients/** - WebClient-based API client implementations for calling connector endpoints
  - `BaseApiClient` - Base class handling authentication (None, Basic, Certificate, API Key), SSL context, and error handling
  - Specific clients extend this for each connector function group

- **model/** - DTOs and enums organized by domain
  - `client/` - Request/response DTOs for client operations
  - `common/` - Shared models (attributes, enums, health)
  - `connector/` - Connector-specific models
  - `core/` - Platform core models

- **exception/** - Custom exception hierarchy (ConnectorException, ValidationException, NotFoundException, etc.)

### Utility Classes (`src/main/java/com/czertainly/core/util/`)

- `AttributeDefinitionUtils` - Attribute content extraction and manipulation
- `AttributeMigrationUtils` / `V2AttributeMigrationUtils` - Database migration helpers for attributes

## Key Patterns

### Connector Function Groups

Connectors must implement specific function groups. Each connector always implements:
- `AttributesController` - List and validate attributes
- `HealthController` - Health check endpoint
- `InfoController` - Connector info and supported function groups

Optional function groups include: Discovery Provider, Authority Provider, Compliance Provider, Cryptography Provider, Entity Provider.

### API Client Communication

Two communication patterns exist:
1. **Direct REST** - `*ApiClient` classes in `clients/` using Spring WebClient
2. **Sync interfaces** - `*SyncApiClient` interfaces in `interfaces/client/` supporting REST or MQ proxy

### OpenAPI Documentation

All controller interfaces use Swagger/OpenAPI v3 annotations (`@Operation`, `@ApiResponses`, `@Tag`) for API documentation generation.

## Testing

Tests use JUnit 5 and WireMock for HTTP mocking. Test files are in `src/test/java/`.