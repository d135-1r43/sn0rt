package de.sn0rt;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
class ShortUrlResourceTest
{
	@Inject
	ShortUrlRepository repository;

	@AfterEach
	@Transactional
	void cleanup()
	{
		repository.deleteAll();
	}

	@Test
	void testShortenUrl()
	{
		// given
		String testUrl = "https://example.com/very/long/url";

		// when & then
		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(200)
			.body("shortCode", notNullValue())
			.body("shortCode", not(emptyString()))
			.body("originalUrl", equalTo(testUrl));
	}

	@Test
	void testShortenUrlWithMissingUrl()
	{
		// given
		// empty request

		// when & then
		given()
			.contentType(ContentType.JSON)
			.body("{}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(400)
			.body("error", equalTo("URL is required"));
	}

	@Test
	void testShortenUrlWithBlankUrl()
	{
		// given
		String blankUrl = "   ";

		// when & then
		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + blankUrl + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(400)
			.body("error", equalTo("URL is required"));
	}

	@Test
	void testRedirect()
	{
		// given
		String testUrl = "https://example.com/destination";
		String shortCode = given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\"}")
			.when()
			.post("/shorten")
			.then()
			.extract()
			.path("shortCode");

		// when & then
		given()
			.redirects().follow(false)
			.when()
			.get("/" + shortCode)
			.then()
			.statusCode(303)
			.header("Location", equalTo(testUrl));
	}

	@Test
	void testRedirectNotFound()
	{
		// given
		String nonExistentCode = "notfound";

		// when & then
		given()
			.when()
			.get("/" + nonExistentCode)
			.then()
			.statusCode(404)
			.body("error", equalTo("Short URL not found"));
	}

	@Test
	void testClickCountIncrement()
	{
		// given
		String testUrl = "https://example.com/test";
		String shortCode = given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\"}")
			.when()
			.post("/shorten")
			.then()
			.extract()
			.path("shortCode");

		// when
		given()
			.redirects().follow(false)
			.when()
			.get("/" + shortCode)
			.then()
			.statusCode(303);

		given()
			.redirects().follow(false)
			.when()
			.get("/" + shortCode)
			.then()
			.statusCode(303);

		// then
		given()
			.when()
			.get("/stats/" + shortCode)
			.then()
			.statusCode(200)
			.body("clickCount", equalTo(2));
	}

	@Test
	void testGetStats()
	{
		// given
		String testUrl = "https://example.com/stats-test";
		String shortCode = given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\"}")
			.when()
			.post("/shorten")
			.then()
			.extract()
			.path("shortCode");

		// when & then
		given()
			.when()
			.get("/stats/" + shortCode)
			.then()
			.statusCode(200)
			.body("shortCode", equalTo(shortCode))
			.body("originalUrl", equalTo(testUrl))
			.body("clickCount", equalTo(0))
			.body("createdAt", notNullValue());
	}

	@Test
	void testGetStatsNotFound()
	{
		// given
		String nonExistentCode = "notfound";

		// when & then
		given()
			.when()
			.get("/stats/" + nonExistentCode)
			.then()
			.statusCode(404)
			.body("error", equalTo("Short URL not found"));
	}

	@Test
	void testMultipleShortenedUrlsHaveUniqueShortCodes()
	{
		// given
		String url1 = "https://example.com/url1";
		String url2 = "https://example.com/url2";

		// when
		String shortCode1 = given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + url1 + "\"}")
			.when()
			.post("/shorten")
			.then()
			.extract()
			.path("shortCode");

		String shortCode2 = given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + url2 + "\"}")
			.when()
			.post("/shorten")
			.then()
			.extract()
			.path("shortCode");

		// then
		assert !shortCode1.equals(shortCode2);
	}

	@Test
	void testShortenUrlWithCustomCode()
	{
		// given
		String testUrl = "https://example.com/custom";
		String customCode = "mycustom";

		// when & then
		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\",\"customCode\":\"" + customCode + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(200)
			.body("shortCode", equalTo(customCode))
			.body("originalUrl", equalTo(testUrl));
	}

	@Test
	void testShortenUrlWithDuplicateCustomCode()
	{
		// given
		String testUrl1 = "https://example.com/first";
		String testUrl2 = "https://example.com/second";
		String customCode = "duplicate";

		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl1 + "\",\"customCode\":\"" + customCode + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(200);

		// when & then
		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl2 + "\",\"customCode\":\"" + customCode + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(409)
			.body("error", equalTo("Custom code already exists"));
	}

	@Test
	void testCustomCodeRedirect()
	{
		// given
		String testUrl = "https://example.com/customdest";
		String customCode = "mylink";

		given()
			.contentType(ContentType.JSON)
			.body("{\"url\":\"" + testUrl + "\",\"customCode\":\"" + customCode + "\"}")
			.when()
			.post("/shorten")
			.then()
			.statusCode(200);

		// when & then
		given()
			.redirects().follow(false)
			.when()
			.get("/" + customCode)
			.then()
			.statusCode(303)
			.header("Location", equalTo(testUrl));
	}
}
