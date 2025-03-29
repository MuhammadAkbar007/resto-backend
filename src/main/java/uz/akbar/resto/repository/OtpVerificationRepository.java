package uz.akbar.resto.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.OtpVerification;

/**
 * OtpVerificationRepository
 */
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

	List<OtpVerification> findByUserIdAndIsVerifiedFalseAndVisibleTrueOrderByCreatedAtDesc(UUID userId);

	void deleteByExpiryTimeBefore(Instant now);
}
