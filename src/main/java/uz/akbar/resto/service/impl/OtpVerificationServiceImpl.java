package uz.akbar.resto.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
				.otp(otp)
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

			if (verification.getOtp().equals(otp) && Instant.now().isBefore(verification.getExpiryTime())) {
				verification.setVerified(true);
				repository.save(verification);

				return true;
			}
		}

		return false;
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
