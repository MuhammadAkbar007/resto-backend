package uz.akbar.resto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.User;

/**
 * UserRepository
 */
public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailOrPhoneNumberAndVisibleTrue(String email, String phoneNumber);
}
