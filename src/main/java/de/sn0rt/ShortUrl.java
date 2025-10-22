package de.sn0rt;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url")
public class ShortUrl extends PanacheEntity
{
	@NotBlank
	@Column(unique = true, nullable = false)
	public String shortCode;

	@NotBlank
	@Column(nullable = false, length = 2048)
	public String originalUrl;

	@Column(nullable = false)
	public LocalDateTime createdAt;

	@Column
	public Long clickCount;

	public ShortUrl()
	{
		this.createdAt = LocalDateTime.now();
		this.clickCount = 0L;
	}

	public ShortUrl(String shortCode, String originalUrl)
	{
		this();
		this.shortCode = shortCode;
		this.originalUrl = originalUrl;
	}
}
