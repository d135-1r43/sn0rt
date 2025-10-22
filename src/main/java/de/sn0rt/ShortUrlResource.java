package de.sn0rt;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShortUrlResource
{
	@Inject
	ShortUrlRepository repository;

	@POST
	@Path("/shorten")
	@Transactional
	public Response shortenUrl(ShortenRequest request)
	{
		if (request.url == null || request.url.isBlank())
		{
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new ErrorResponse("URL is required"))
				.build();
		}

		String shortCode;
		if (request.customCode != null && !request.customCode.isBlank())
		{
			shortCode = request.customCode;
			if (repository.existsByShortCode(shortCode))
			{
				return Response.status(Response.Status.CONFLICT)
					.entity(new ErrorResponse("Custom code already exists"))
					.build();
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

		ShortUrl shortUrl = new ShortUrl(shortCode, request.url);
		repository.persist(shortUrl);

		return Response.ok(new ShortenResponse(shortCode, shortUrl.originalUrl)).build();
	}

	@GET
	@Path("/{shortCode}")
	@Transactional
	public Response redirect(@PathParam("shortCode") String shortCode)
	{
		return repository.findByShortCode(shortCode)
			.map(shortUrl -> {
				shortUrl.clickCount++;
				repository.persist(shortUrl);
				return Response.seeOther(URI.create(shortUrl.originalUrl)).build();
			})
			.orElse(Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorResponse("Short URL not found"))
				.build());
	}

	@GET
	@Path("/stats/{shortCode}")
	public Response getStats(@PathParam("shortCode") String shortCode)
	{
		return repository.findByShortCode(shortCode)
			.map(shortUrl -> Response.ok(new StatsResponse(
				shortUrl.shortCode,
				shortUrl.originalUrl,
				shortUrl.clickCount,
				shortUrl.createdAt.toString())).build())
			.orElse(Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorResponse("Short URL not found"))
				.build());
	}

	private String generateShortCode()
	{
		return UUID.randomUUID().toString().substring(0, 8);
	}

	public static class ShortenRequest
	{
		public String url;
		public String customCode;
	}

	public static class ShortenResponse
	{
		public String shortCode;
		public String originalUrl;

		public ShortenResponse(String shortCode, String originalUrl)
		{
			this.shortCode = shortCode;
			this.originalUrl = originalUrl;
		}
	}

	public static class StatsResponse
	{
		public String shortCode;
		public String originalUrl;
		public Long clickCount;
		public String createdAt;

		public StatsResponse(String shortCode, String originalUrl, Long clickCount, String createdAt)
		{
			this.shortCode = shortCode;
			this.originalUrl = originalUrl;
			this.clickCount = clickCount;
			this.createdAt = createdAt;
		}
	}

	public static class ErrorResponse
	{
		public String error;

		public ErrorResponse(String error)
		{
			this.error = error;
		}
	}
}
