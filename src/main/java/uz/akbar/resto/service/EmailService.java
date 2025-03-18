package uz.akbar.resto.service;

import java.util.UUID;

/**
 * EmailService
 */
public interface EmailService {

	void sendOtpEmail(String email, String otp, int expiryMinutes);

	void sendRegistrationEmail(String email, UUID userId);
}
