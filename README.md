# API Automation Testing Framework - Online Bookstore

[![ci](https://github.com/DanielDelimata/bookstore-cucumber/actions/workflows/ci.yml/badge.svg)](https://github.com/DanielDelimata/bookstore-cucumber/actions/workflows/ci.yml)

## Overview
Comprehensive API automation testing framework for the [FakeRestAPI](https://fakerestapi.azurewebsites.net/index.html) bookstore endpoints using Java, RestAssured, JUnit,
and Docker.
The framework supports local execution, Docker-based execution, and CI/CD integration with Allure reporting.

## Prerequisites
- Java
- Gradle
- Docker (optional)
- Git

## Quick Start

[//]: # (TODO add screenshots)
### Local Execution

```bash

# Clone repository
git clone https://github.com/DanielDelimata/bookstore-cucumber.git

# Run tests
gradle clean test

# Run with custom base URL
gradle clean test -Dbase.url=https://fakerestapi.azurewebsites.net

# Serve Allure report locally
gradle allureServe
```

### Docker Execution (local)

Ensure Docker is installed and running on your machine.

```bash
docker compose -f docker-compose.yml -p bookstore-cucumber up -d
```

Access the report at http://localhost:8080.

### CI/CD Integration

The project includes a GitHub Actions workflow for automated testing and Allure report generation on each push.
The workflow is defined in `.github/workflows/ci.yml`.
The tests are executed in a Docker container.
The Allure report is published as a GitHub Pages site.


URL: https://DanielDelimata.github.io/bookstore-cucumber/
