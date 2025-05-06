package uz.akbar.resto.config;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.repository.RoleRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.AttachmentService;

/**
 * DataInitializer
 */
@Configuration
public class DataInitializer {

	@Value("${admin.default.email}")
	private String adminEmail;

	@Value("${admin.default.phoneNumber}")
	private String adminPhoneNumber;

	@Value("${admin.default.password}")
	private String adminPassword;

	@Value("${admin.default.firstName}")
	private String adminFirstName;

	@Value("${admin.default.lastName}")
	private String adminLastName;

	@Bean
	@Transactional
	public CommandLineRunner initData(
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AttachmentService attachmentService) {
		return args -> {

			// create employeeRole if not exists
			if (!roleRepository.existsByRoleType(RoleType.ROLE_EMPLOYEE)) {
				roleRepository.save(
						Role.builder()
								.roleType(RoleType.ROLE_EMPLOYEE)
								.description("Employee role")
								.visible(true)
								.createdAt(Instant.now())
								.build());
			}

			// create managerRole if not exists
			if (!roleRepository.existsByRoleType(RoleType.ROLE_MANAGER)) {
				roleRepository.save(
						Role.builder()
								.roleType(RoleType.ROLE_MANAGER)
								.description("Manager role")
								.visible(true)
								.createdAt(Instant.now())
								.build());
			}

			// create customerRole if not exists to attach admin&user
			Role customerRole = roleRepository.findByRoleType(RoleType.ROLE_CUSTOMER)
					.orElseGet(() -> {
						Role role = Role.builder()
								.roleType(RoleType.ROLE_CUSTOMER)
								.description("Customer role")
								.visible(true)
								.createdAt(Instant.now())
								.build();

						return roleRepository.save(role);
					});

			// create adminRole if not exists to attach admin&user
			Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
					.orElseGet(() -> {
						Role role = Role.builder()
								.roleType(RoleType.ROLE_ADMIN)
								.description("Customer admin")
								.visible(true)
								.createdAt(Instant.now())
								.build();

						return roleRepository.save(role);
					});

			// create admin&user User if not exists
			if (!userRepository.existsByEmailOrPhoneNumber(adminEmail, adminPhoneNumber)) {
				User adminUser = User.builder()
						.firstName(adminFirstName)
						.lastName(adminLastName)
						.email(adminEmail)
						.phoneNumber(adminPhoneNumber)
						.password(passwordEncoder.encode(adminPassword))
						.status(GeneralStatus.ACTIVE)
						.photo(attachmentService.getDefaultProfileImage())
						.roles(Set.of(customerRole, adminRole))
						.visible(true)
						.createdAt(Instant.now())
						.build();

				userRepository.save(adminUser);
			}
		};
	}
}
