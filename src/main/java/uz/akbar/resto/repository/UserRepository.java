package uz.akbar.resto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import uz.akbar.resto.entity.User;

/**
 * UserRepository
 */
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

	boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<User> findByEmail(String email);

	Optional<User> findByIdAndVisibleTrue(UUID id);

	Optional<User> findByEmailOrPhoneNumberAndVisibleTrue(String email, String phoneNumber);
}
