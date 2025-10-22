package de.sn0rt;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

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

@Path("/admin")
public class AdminPage
{
	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance admin(List<ShortUrl> urls, String error, String success);
	}

	@Inject
	ShortUrlRepository repository;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance get(@QueryParam("error") String error, @QueryParam("success") String success)
	{
		List<ShortUrl> urls = repository.listAll();
		return Templates.admin(urls, error, success);
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

	private String generateShortCode()
	{
		return UUID.randomUUID().toString().substring(0, 8);
	}
}