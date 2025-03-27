package uz.akbar.resto.service;

import java.util.UUID;

/**
 * OtpVerificationService
 */
public interface OtpVerificationService {

	String createOtp(UUID userId);

	boolean verifyOtp(String otp, UUID userId);

	void cleanupExpiredOtps();
}
