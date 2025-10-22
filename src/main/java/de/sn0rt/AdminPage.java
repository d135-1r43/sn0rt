package de.sn0rt;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/admin")
@RolesAllowed("admin")
public class AdminPage
{
	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance admin(List<UrlWithQrCode> urls, String error, String success);
	}

	@Inject
	ShortUrlRepository repository;

	@Inject
	QRCodeService qrCodeService;

	@Inject
	PdfService pdfService;

	@ConfigProperty(name = "sn0rt.base-url")
	String baseUrl;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance get(@QueryParam("error") String error, @QueryParam("success") String success)
	{
		List<ShortUrl> urls = repository.listAll();
		List<UrlWithQrCode> urlsWithQrCodes = urls.stream()
			.map(url -> {
				String fullUrl = baseUrl + "/" + url.shortCode;
				String qrCodeBase64 = qrCodeService.toBase64Image(qrCodeService.generateQrCode(fullUrl), 4, 2);
				return new UrlWithQrCode(url, qrCodeBase64);
			})
			.collect(Collectors.toList());
		return Templates.admin(urlsWithQrCodes, error, success);
	}

	public record UrlWithQrCode(ShortUrl url, String qrCodeBase64)
	{
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Transactional
	public Response createShortUrl(@FormParam("url") String url, @FormParam("customCode") String customCode)
	{
		if (url == null || url.isBlank())
		{
			return Response.seeOther(URI.create("/admin?error=" + URLEncoder.encode("URL is required", StandardCharsets.UTF_8))).build();
		}

		String shortCode;
		if (customCode != null && !customCode.isBlank())
		{
			shortCode = customCode;
			if (repository.existsByShortCode(shortCode))
			{
				return Response.seeOther(URI.create("/admin?error=" + URLEncoder.encode("Custom code already exists", StandardCharsets.UTF_8))).build();
			}
		}
		else
		{
			shortCode = generateShortCode();
			while (repository.existsByShortCode(shortCode))
			{
				shortCode = generateShortCode();
			}
		}

		ShortUrl shortUrl = new ShortUrl(shortCode, url);
		repository.persist(shortUrl);

		return Response.seeOther(URI.create("/admin?success=" + URLEncoder.encode("Short URL created: " + shortCode, StandardCharsets.UTF_8))).build();
	}

	@GET
	@Path("/qr/{shortCode}/pdf")
	@Produces("application/pdf")
	public Response downloadQrCodePdf(@PathParam("shortCode") String shortCode)
	{
		return repository.findByShortCode(shortCode)
			.map(shortUrl -> {
				try
				{
					String fullUrl = baseUrl + "/" + shortUrl.shortCode;
					byte[] pdf = pdfService.generateQrCodePdf(shortUrl, fullUrl);
					return Response.ok(pdf)
						.header("Content-Disposition", "attachment; filename=qr-" + shortCode + ".pdf")
						.build();
				}
				catch (Exception e)
				{
					return Response.serverError()
						.entity("Failed to generate PDF: " + e.getMessage())
						.build();
				}
			})
			.orElse(Response.status(Response.Status.NOT_FOUND)
				.entity("Short URL not found")
				.build());
	}

	private String generateShortCode()
	{
		return UUID.randomUUID().toString().substring(0, 8);
	}
}