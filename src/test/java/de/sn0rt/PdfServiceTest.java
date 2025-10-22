package de.sn0rt;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PdfServiceTest
{
	@Inject
	PdfService pdfService;

	@Test
	void testGenerateQrCodePdfCreatesValidPdf() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("testcode", "https://example.com/original");
		String fullUrl = "http://localhost:8080/testcode";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		assertNotNull(pdfBytes);
		assertTrue(pdfBytes.length > 0);

		// Verify PDF magic number
		assertEquals(0x25, pdfBytes[0]); // %
		assertEquals(0x50, pdfBytes[1]); // P
		assertEquals(0x44, pdfBytes[2]); // D
		assertEquals(0x46, pdfBytes[3]); // F
	}

	@Test
	void testGenerateQrCodePdfContainsExpectedText() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("mycode", "https://example.com/test");
		String fullUrl = "http://localhost:8080/mycode";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		String pdfText = extractTextFromPdf(pdfBytes);

		assertTrue(pdfText.contains("QR Code for Short URL"));
		assertTrue(pdfText.contains("Short Code: mycode"));
		assertTrue(pdfText.contains("URL: http://localhost:8080/mycode"));
		assertTrue(pdfText.contains("Redirects to: https://example.com/test"));
	}

	@Test
	void testGenerateQrCodePdfWithDifferentShortCodes() throws IOException
	{
		// given
		ShortUrl shortUrl1 = new ShortUrl("code1", "https://example.com/url1");
		ShortUrl shortUrl2 = new ShortUrl("code2", "https://example.com/url2");
		String fullUrl1 = "http://localhost:8080/code1";
		String fullUrl2 = "http://localhost:8080/code2";

		// when
		byte[] pdf1 = pdfService.generateQrCodePdf(shortUrl1, fullUrl1);
		byte[] pdf2 = pdfService.generateQrCodePdf(shortUrl2, fullUrl2);

		// then
		assertNotNull(pdf1);
		assertNotNull(pdf2);
		assertNotEquals(pdf1.length, pdf2.length); // Different content,
													// different sizes

		String text1 = extractTextFromPdf(pdf1);
		String text2 = extractTextFromPdf(pdf2);

		assertTrue(text1.contains("code1"));
		assertTrue(text2.contains("code2"));
		assertFalse(text1.contains("code2"));
		assertFalse(text2.contains("code1"));
	}

	@Test
	void testGenerateQrCodePdfContainsImage() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("imgtest", "https://example.com/image");
		String fullUrl = "http://localhost:8080/imgtest";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		try (PDDocument document = Loader.loadPDF(pdfBytes))
		{
			assertEquals(1, document.getNumberOfPages());

			// Verify the PDF has resources (images)
			var page = document.getPage(0);
			assertNotNull(page.getResources());

			// Check if there are XObject resources (images)
			var xObjects = page.getResources().getXObjectNames();
			assertTrue(xObjects.iterator().hasNext(), "PDF should contain at least one image (QR code)");
		}
	}

	@Test
	void testGenerateQrCodePdfWithLongUrls() throws IOException
	{
		// given
		String longUrl = "https://example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2&param3=value3";
		ShortUrl shortUrl = new ShortUrl("short", longUrl);
		String fullUrl = "http://localhost:8080/short";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		assertNotNull(pdfBytes);
		assertTrue(pdfBytes.length > 0);

		String pdfText = extractTextFromPdf(pdfBytes);
		assertTrue(pdfText.contains("short"));
		assertTrue(pdfText.contains(longUrl));
	}

	@Test
	void testGenerateQrCodePdfWithSpecialCharacters() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("test123", "https://example.com/path?query=test&foo=bar");
		String fullUrl = "http://localhost:8080/test123";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		assertNotNull(pdfBytes);
		String pdfText = extractTextFromPdf(pdfBytes);

		assertTrue(pdfText.contains("test123"));
		assertTrue(pdfText.contains("https://example.com/path?query=test&foo=bar"));
	}

	@Test
	void testGenerateQrCodePdfCreatesValidDocumentEachTime() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("test", "https://example.com/test");
		String fullUrl = "http://localhost:8080/test";

		// when
		byte[] pdf1 = pdfService.generateQrCodePdf(shortUrl, fullUrl);
		byte[] pdf2 = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		assertNotNull(pdf1);
		assertNotNull(pdf2);
		assertTrue(pdf1.length > 0);
		assertTrue(pdf2.length > 0);

		// Both should contain the same text content
		String text1 = extractTextFromPdf(pdf1);
		String text2 = extractTextFromPdf(pdf2);
		assertEquals(text1, text2);
	}

	@Test
	void testGenerateQrCodePdfClosesResourcesProperly() throws IOException
	{
		// given
		ShortUrl shortUrl = new ShortUrl("resource", "https://example.com/resource");
		String fullUrl = "http://localhost:8080/resource";

		// when
		byte[] pdfBytes = pdfService.generateQrCodePdf(shortUrl, fullUrl);

		// then
		// Should be able to load the PDF without issues
		try (PDDocument document = Loader.loadPDF(pdfBytes))
		{
			assertNotNull(document);
			assertEquals(1, document.getNumberOfPages());
		}
		// If resources weren't closed properly, this would fail or cause issues
	}

	// Helper methods

	private String extractTextFromPdf(byte[] pdfBytes) throws IOException
	{
		try (PDDocument document = Loader.loadPDF(pdfBytes))
		{
			PDFTextStripper stripper = new PDFTextStripper();
			return stripper.getText(document);
		}
	}
}
