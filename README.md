<div align="center">

<img src="docs/sn0rt-logo.png" alt="sn0rt logo" width="200"/>

# sn0rt

### *The World's Best URL Snortener™* 🐷

**Why have long URLs when you can have short ones?**
We snort 'em down to size! Because nobody has time for 47-character URLs when 8 will do.

[![Built with Quarkus](https://img.shields.io/badge/Built%20with-Quarkus-4695EB?style=for-the-badge&logo=quarkus)](https://quarkus.io/)
[![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)

</div>

---

## ✨ Features (aka Snort Powers)

| Feature | Description |
|---------|-------------|
| ⚡ **Snort Speed** | Faster than a pig finding truffles. URL snorting in milliseconds! |
| 🎯 **Custom Snorts** | Name your links whatever you want. Go wild! Be creative! Snort responsibly. |
| 📊 **Snort Analytics** | See how many times your link got snorted. Yes, we count every single snort. |
| 📱 **QR Snorts** | Auto-magical QR codes! Point, scan, snort. It's that easy. |
| 🔒 **Secure Snorting** | Your URLs are safe with us. We take our snorting very seriously. |
| 🎨 **Pretty Snorts** | Because if you're gonna snort URLs, they better look fabulous doing it. |

## 🚀 Quick Start (Get Snorting!)

### What You'll Need

- **JDK 21** - Because we're fancy like that
- **PostgreSQL** - For storing all your glorious snorts (or use Docker Compose)
- **Maven** - The wrapper is included, you're welcome

### Fire It Up! 🔥

Start the application with live reload (snort reloading?):

```bash
./mvnw quarkus:dev
```

**Boom!** 💥 Your snortener is now running at:
- 🏠 **Homepage**: http://localhost:8080 - The fabulous landing page
- 👑 **Admin Panel**: http://localhost:8080/admin - Where the magic happens (default: `admin`/`admin`)
- 🛠️ **Dev UI**: http://localhost:8080/q/dev/ - For the curious minds
- 📚 **API Docs**: http://localhost:8080/swagger-ui - Because documentation matters

### ⚙️ Configuration (Make It Yours)

**Pro tip**: Don't use the default `admin`/`admin` credentials in production. That's just asking for trouble. 🙈

Set your admin credentials via environment variables:

```bash
export ADMIN_USERNAME=your-username
export ADMIN_PASSWORD=super-secret-password
```

Or configure in `application.properties`:

```properties
sn0rt.admin.username=your-username
sn0rt.admin.password=super-secret-password
sn0rt.base-url=https://your-domain.com
```

## 🐳 Docker Deployment (Containerized Snorting)

### Build Your Snort Container

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

### 📦 GitHub Container Registry

We've got CI/CD! Every push to `main` automatically builds and publishes a fresh Docker image:

```bash
docker pull ghcr.io/d135-1r43/sn0rt:latest
```

## 🔨 Building and Testing

### Run Tests (Make Sure Nothing's Broken)

```bash
./mvnw test
```

All tests follow the **Given-When-Then** pattern. We're civilized here. 🎩

### Build Package

```bash
./mvnw package
```

### Build Uber-JAR (The Chonky Boi)

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

## ⚡ Native Executable (For Speed Demons)

Want blazing-fast startup and tiny memory footprint? Go native!

```bash
./mvnw package -Dnative
```

No GraalVM? No problem! Build in a container:

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Run your lightning-fast native snortener:

```bash
./target/sn0rt-1.0.0-SNAPSHOT-runner
```

> **Note**: Native builds can take a while. Perfect time for a coffee break ☕

## 🎯 API Usage (For the Programmers)

### Create a Short URL

**Option 1: The Clicky Way** 🖱️
Just go to http://localhost:8080/admin and use the fancy web form.

**Option 2: The Cool Way** 😎

```bash
# Let us pick a random code for you
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/very/long/url"}'

# Be fancy with a custom code
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://example.com/very/long/url", "customCode": "my-awesome-link"}'
```

### Use Your Snorted URL

```bash
curl -L http://localhost:8080/{shortCode}
```

It redirects! Like magic! ✨

### Get Stats

```bash
curl http://localhost:8080/stats/{shortCode}
```

### Download QR Code PDF

```bash
curl -u admin:admin http://localhost:8080/admin/qr/{shortCode}/pdf -o qr-code.pdf
```

Perfect for printing and sticking on everything! 📄

## 🛠️ Tech Stack (The Good Stuff)

Built with love and these awesome technologies:

| Technology | What It Does |
|------------|--------------|
| [Quarkus 3.28.4](https://quarkus.io/) | The supersonic, subatomic Java framework |
| Java 21 | Because we like our Java fresh and modern |
| PostgreSQL | Where all the snorts are stored |
| Hibernate ORM Panache | Makes database stuff not painful |
| Qute | Templating that doesn't make you cry |
| [Quarkus QR Code](https://docs.quarkiverse.io/quarkus-barcode/dev/index.html) | For those fancy scannable squares |
| [Quarkus PDFBox](https://docs.quarkiverse.io/quarkus-pdfbox/dev/index.html) | PDF generation without the headache |
| Quarkus Security JPA | Keeps the bad guys out with BCrypt |
| GitHub Actions | Automatic builds because manual is for chumps |

## 🤝 Contributing

Found a bug? Want to add a feature? Have a hilarious idea for the copy?

**Contributions are welcome!** Please feel free to submit a Pull Request. Just remember:
- Keep it snorty 🐷
- Follow the Given-When-Then test pattern
- Make it fabulous ✨

## 📝 License

This project uses the Quarkus framework under the Apache License 2.0.

---

<div align="center">

**Made with 💖 and 🐷 snorts**

[⭐ Star us on GitHub](https://github.com/d135-1r43/sn0rt) • [🐛 Report Bug](https://github.com/d135-1r43/sn0rt/issues) • [✨ Request Feature](https://github.com/d135-1r43/sn0rt/issues)

</div>
