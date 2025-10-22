package de.sn0rt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ApplicationScoped
public class PdfService
{
	@Inject
	QRCodeService qrCodeService;

	public byte[] generateQrCodePdf(ShortUrl shortUrl, String fullUrl) throws IOException
	{
		PDDocument document = new PDDocument();
		try
		{
			PDPage page = new PDPage();
			document.addPage(page);

			// Generate QR code image
			BufferedImage qrImage = qrCodeService.toImage(qrCodeService.generateQrCode(fullUrl), 8, 4);

			// Convert BufferedImage to PDImageXObject
			PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);

			// Add content to PDF
			PDPageContentStream contentStream = new PDPageContentStream(document, page);

			// Add title
			contentStream.beginText();
			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
			contentStream.newLineAtOffset(50, 750);
			contentStream.showText("QR Code for Short URL");
			contentStream.endText();

			// Add short code
			contentStream.beginText();
			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
			contentStream.newLineAtOffset(50, 720);
			contentStream.showText("Short Code: " + shortUrl.shortCode);
			contentStream.endText();

			// Add full URL
			contentStream.beginText();
			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
			contentStream.newLineAtOffset(50, 695);
			contentStream.showText("URL: " + fullUrl);
			contentStream.endText();

			// Add original URL
			contentStream.beginText();
			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
			contentStream.newLineAtOffset(50, 675);
			contentStream.showText("Redirects to: " + shortUrl.originalUrl);
			contentStream.endText();

			// Add QR code image
			contentStream.drawImage(pdImage, 150, 350, 300, 300);

			contentStream.close();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			document.save(baos);
			return baos.toByteArray();
		}
		finally
		{
			document.close();
		}
	}
}
