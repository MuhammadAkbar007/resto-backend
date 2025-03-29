package uz.akbar.resto.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.OtpVerification;
import uz.akbar.resto.repository.OtpVerificationRepository;
import uz.akbar.resto.service.OtpVerificationService;

/**
 * OtpVerificationServiceImpl
 */
@RequiredArgsConstructor
@Getter
@Service
public class OtpVerificationServiceImpl implements OtpVerificationService {

	private final OtpVerificationRepository repository;
	private final BCryptPasswordEncoder encoder;

	@Value("${app.otp.expiryMinutes:15}")
	private int expiryMinutes;

	@Value("${app.otp.length:6}")
	private int otpLength;

	@Override
	@Transactional
	public String createOtp(UUID userId) {
		String otp = generateOtp(otpLength);

		OtpVerification otpVerification = OtpVerification.builder()
				.userId(userId)
				.otp(encoder.encode(otp))
				.expiryTime(Instant.now().plusSeconds(expiryMinutes * 60))
				.createdAt(Instant.now())
				.isVerified(false)
				.visible(true)
				.build();

		repository.save(otpVerification);

		return otp;
	}

	@Override
	@Transactional
	public boolean verifyOtp(String otp, UUID userId) {

		List<OtpVerification> verifications = repository
				.findByUserIdAndIsVerifiedFalseAndVisibleTrueOrderByCreatedAtDesc(userId);

		for (OtpVerification verification : verifications) {
			if (verification.getAttemptCount() >= 5) {
				repository.delete(verification);
				repository.flush();
				return false;
			}

			if (encoder.matches(otp, verification.getOtp()) && Instant.now().isBefore(verification.getExpiryTime())) {
				repository.delete(verification);
				repository.flush();
				return true;
			}

			verification.incrementAttempt();
			repository.save(verification);
		}

		return false;
	}

	@Override
	@Transactional
	@Scheduled(fixedRateString = "${app.otp.cleanup-interval:86400000}") // run daily 86.400.000 ms
	public void cleanupExpiredOtps() {
		repository.deleteByExpiryTimeBefore(Instant.now());
	}

	private String generateOtp(int length) {
		SecureRandom random = new SecureRandom();
		StringBuilder otp = new StringBuilder();

		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}

		return otp.toString();
	}

}
