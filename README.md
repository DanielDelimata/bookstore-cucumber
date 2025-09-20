# API Automation Testing Framework - Online Bookstore

## Overview
Comprehensive API automation testing framework for the FakeRestAPI bookstore endpoints using Java, RestAssured, JUnit,
and Docker.
The framework supports local execution, Docker-based execution, and CI/CD integration with Allure reporting.

## Prerequisites
- Java
- Gradle
- Docker (optional)
- Git

## Quick Start

[//]: # (TODO correct repository name, add screenshots, badges, and other relevant information)
### Local Execution
```bash
# Clone repository
git clone <repository-url>
cd api-automation-bookstore

# Run tests
gradle clean test

# Run with custom base URL
gradle clean test -Dbase.url=https://fakerestapi.azurewebsites.net

# Serve Allure report locally
gradle allureServe
```

### Docker Execution

Ensure Docker is installed and running on your machine.

```bash
# Build Docker image
docker build -t api-automation-bookstore .
# Run tests in Docker container
docker run --rm api-automation-bookstore
```
Serve Allure report from Docker container
```bash
docker run --rm -p 8080:8080 api-automation-bookstore gradle
allureServe
```
Access the report at `http://localhost:8080`.

### CI/CD Integration
The project includes a GitHub Actions workflow for automated testing and Allure report generation on each push.
The workflow is defined in `.github/workflows/ci.yml`.

## Configuration
You can customize the test execution by passing parameters via the command line or environment variables.
- `base.url`: Base URL of the API (default: `https://fakerestapi.azurewebsites.net`).

## Project Structure

- `src/test/java`: Contains test classes and utility classes.
- `build.gradle`: Gradle build configuration file.
- `Dockerfile`: Docker configuration file for containerizing the tests.
- `allure-results`: Directory where Allure results are stored.
- `allure-report`: Directory where the generated Allure report is stored.
- `README.md`: Project documentation.
- `.gitignore`: Specifies files and directories to be ignored by Git.
- `gradlew` and `gradlew.bat`: Gradle wrapper scripts for Unix and Windows systems, respectively.
- `gradle/wrapper`: Contains Gradle wrapper JAR and properties files.
- `settings.gradle`: Gradle settings file.
- `.github/workflows`: Contains GitHub Actions workflow files for CI/CD.
