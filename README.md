# sn0rt 🐷

A URL shortening service built with Quarkus featuring QR code generation and a synthwave-styled admin interface.

## Features

- 🔗 **URL Shortening** - Create short URLs with auto-generated or custom codes
- 📱 **QR Code Generation** - Automatically generate QR codes for each short URL
- 📄 **PDF Export** - Download QR codes as PDFs
- 🔐 **Secure Admin Panel** - Basic authentication with configurable credentials
- 🌈 **Synthwave UI** - Pig pink styled admin interface with glassmorphism effects
- 🚀 **Built with Quarkus** - Fast, lightweight, and cloud-native

## Quick Start

### Prerequisites

- JDK 21
- PostgreSQL database (or use Docker Compose)
- Maven

### Running in Development Mode

Start the application with live reload:

```bash
./mvnw quarkus:dev
```

The application will be available at:
- **Homepage**: http://localhost:8080
- **Admin Panel**: http://localhost:8080/admin (default credentials: `admin`/`admin`)
- **Dev UI**: http://localhost:8080/q/dev/

### Configuration

Configure admin credentials via environment variables:

```bash
export ADMIN_USERNAME=your-username
export ADMIN_PASSWORD=your-password
```

Or in `application.properties`:

```properties
sn0rt.admin.username=your-username
sn0rt.admin.password=your-password
sn0rt.base-url=https://your-domain.com
```

## Docker Deployment

### Build Docker Image

```bash
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t sn0rt:latest .
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e ADMIN_USERNAME=admin \
  -e ADMIN_PASSWORD=secure-password \
  -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://host.docker.internal:5432/sn0rt \
  -e QUARKUS_DATASOURCE_USERNAME=postgres \
  -e QUARKUS_DATASOURCE_PASSWORD=postgres \
  sn0rt:latest
```

### GitHub Container Registry

The project includes a GitHub Actions workflow that automatically builds and pushes Docker images to GitHub Container Registry on every push to `main`:

```bash
docker pull ghcr.io/<your-username>/sn0rt:latest
```

## Building and Testing

### Run Tests

```bash
./mvnw test
```

### Build Package

```bash
./mvnw package
```

### Build Uber-JAR

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

## Native Executable

Build a native executable for faster startup and lower memory footprint:

```bash
./mvnw package -Dnative
```

Or build in a container (no GraalVM installation required):

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Run the native executable:

```bash
./target/sn0rt-1.0.0-SNAPSHOT-runner
```

Learn more: [Quarkus Native Builds](https://quarkus.io/guides/maven-tooling)

## API Usage

### Create a Short URL

**Via Admin Panel:**
Navigate to http://localhost:8080/admin and use the web form.

**Via REST API:**

```bash
# Auto-generated short code
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/very/long/url"}'

# Custom short code
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/very/long/url", "customCode": "my-link"}'
```

### Access a Short URL

```bash
curl -L http://localhost:8080/{shortCode}
```

### Download QR Code PDF

```bash
curl -u admin:admin http://localhost:8080/admin/qr/{shortCode}/pdf -o qr-code.pdf
```

## Tech Stack

- **Framework**: [Quarkus 3.28.4](https://quarkus.io/)
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Hibernate ORM with Panache
- **Templating**: Qute
- **QR Code**: [Quarkus QR Code Generator](https://docs.quarkiverse.io/quarkus-barcode/dev/index.html)
- **PDF**: [Quarkus PDFBox](https://docs.quarkiverse.io/quarkus-pdfbox/dev/index.html)
- **Security**: Quarkus Security JPA with BCrypt
- **CI/CD**: GitHub Actions

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project uses the Quarkus framework under the Apache License 2.0.
