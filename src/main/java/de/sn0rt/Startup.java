package de.sn0rt;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Startup
{
	@ConfigProperty(name = "sn0rt.admin.username", defaultValue = "admin")
	String adminUsername;

	@ConfigProperty(name = "sn0rt.admin.password", defaultValue = "admin")
	String adminPassword;

	@Transactional
	public void loadUsers(@Observes StartupEvent evt)
	{
		// Only create admin user if it doesn't exist
		if (User.find("username", adminUsername).count() == 0)
		{
			User admin = new User();
			admin.username = adminUsername;
			admin.password = BcryptUtil.bcryptHash(adminPassword);
			admin.role = "admin";
			admin.persist();
		}
	}
}
