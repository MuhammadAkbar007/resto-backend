package uz.akbar.resto.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import uz.akbar.resto.entity.Role;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.repository.RoleRepository;

/**
 * DataInitializer
 */
@Configuration
public class DataInitializer {

	@Bean
	@Transactional
	public CommandLineRunner initData(RoleRepository roleRepository) {
		return args -> {
			// create customerRole if doesn't exist
			roleRepository.findByRoleType(RoleType.CUSTOMER).orElseGet(() -> {
				Role role = new Role();
				role.setRoleType(RoleType.CUSTOMER);
				role.setVisible(true);
				role.setCreatedAt(Instant.now());

				return roleRepository.save(role);
			});
		};
	}
}
