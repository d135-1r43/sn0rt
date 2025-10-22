package de.sn0rt;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ShortUrlRepositoryTest
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
	@Transactional
	void testPersistAndFindByShortCode()
	{
		// given
		String shortCode = "abc123";
		String url = "https://example.com";
		ShortUrl shortUrl = new ShortUrl(shortCode, url);

		// when
		repository.persist(shortUrl);
		Optional<ShortUrl> found = repository.findByShortCode(shortCode);

		// then
		assertTrue(found.isPresent());
		assertEquals(shortCode, found.get().shortCode);
		assertEquals(url, found.get().originalUrl);
		assertNotNull(found.get().createdAt);
		assertEquals(0L, found.get().clickCount);
	}

	@Test
	@Transactional
	void testFindByShortCodeNotFound()
	{
		// given
		String nonExistentCode = "notfound";

		// when
		Optional<ShortUrl> found = repository.findByShortCode(nonExistentCode);

		// then
		assertFalse(found.isPresent());
	}

	@Test
	@Transactional
	void testExistsByShortCode()
	{
		// given
		String shortCode = "exists123";
		String url = "https://example.com";
		ShortUrl shortUrl = new ShortUrl(shortCode, url);
		repository.persist(shortUrl);

		// when
		boolean exists = repository.existsByShortCode(shortCode);
		boolean notExists = repository.existsByShortCode("notexists");

		// then
		assertTrue(exists);
		assertFalse(notExists);
	}

	@Test
	@Transactional
	void testClickCountUpdate()
	{
		// given
		String shortCode = "click123";
		String url = "https://example.com";
		ShortUrl shortUrl = new ShortUrl(shortCode, url);
		repository.persist(shortUrl);

		// when
		ShortUrl found = repository.findByShortCode(shortCode).orElseThrow();
		found.clickCount++;
		repository.persist(found);

		// then
		ShortUrl updated = repository.findByShortCode(shortCode).orElseThrow();
		assertEquals(1L, updated.clickCount);
	}

	@Test
	@Transactional
	void testMultipleShortUrls()
	{
		// given
		ShortUrl url1 = new ShortUrl("code1", "https://example.com/1");
		ShortUrl url2 = new ShortUrl("code2", "https://example.com/2");
		ShortUrl url3 = new ShortUrl("code3", "https://example.com/3");

		// when
		repository.persist(url1);
		repository.persist(url2);
		repository.persist(url3);

		// then
		assertEquals(3, repository.count());
		assertTrue(repository.existsByShortCode("code1"));
		assertTrue(repository.existsByShortCode("code2"));
		assertTrue(repository.existsByShortCode("code3"));
	}
}
