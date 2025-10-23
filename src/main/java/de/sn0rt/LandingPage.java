package de.sn0rt;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class LandingPage
{
	@Inject
	ShortUrlRepository repository;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance index();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance getHome()
	{
		return Templates.index();
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
			.orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
				.type(MediaType.TEXT_HTML)
				.entity(generateNotFoundHtml(shortCode))
				.build());
	}

	private String generateNotFoundHtml(String shortCode)
	{
		return """
			<!DOCTYPE html>
			<html>
			<head>
				<title>Short URL Not Found - sn0rt</title>
				<meta charset="UTF-8">
				<meta http-equiv="refresh" content="3;url=/" />
				<style>
					body {
						background: linear-gradient(180deg, %s 0%%, %s 50%%, %s 100%%);
						color: %s;
						font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
						display: flex;
						align-items: center;
						justify-content: center;
						min-height: 100vh;
						margin: 0;
						text-align: center;
					}
					.container {
						background: rgba(45, 10, 61, 0.5);
						border: 2px solid %s;
						border-radius: 20px;
						padding: 40px;
						box-shadow: 0 0 30px rgba(255, 127, 183, 0.5);
					}
					h1 { color: %s; font-size: 3rem; margin: 0 0 20px 0; }
					p { color: %s; font-size: 1.1rem; }
					code { background: rgba(26, 5, 32, 0.6); padding: 5px 10px; border-radius: 5px; }
				</style>
			</head>
			<body>
				<div class="container">
					<h1>404</h1>
					<p>Short URL <code>%s</code> not found</p>
					<p>Redirecting to home page...</p>
				</div>
			</body>
			</html>
			""".formatted("#1a0520", "#2d0a3d", "#1a0520", "#ff7fb7", "#ff7fb7", "#ff1493", "#ffb3d9", shortCode);
	}
}
