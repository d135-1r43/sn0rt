package de.sn0rt;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ShortUrlRepository implements PanacheRepository<ShortUrl>
{
	public Optional<ShortUrl> findByShortCode(String shortCode)
	{
		return find("shortCode", shortCode).firstResultOptional();
	}

	public boolean existsByShortCode(String shortCode)
	{
		return count("shortCode", shortCode) > 0;
	}
}
