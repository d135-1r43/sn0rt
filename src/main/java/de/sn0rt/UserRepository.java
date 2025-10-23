package de.sn0rt;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User>
{
	public Optional<User> findByUsername(String username)
	{
		return find("username", username).firstResultOptional();
	}

	public long countByUsername(String username)
	{
		return count("username", username);
	}
}
