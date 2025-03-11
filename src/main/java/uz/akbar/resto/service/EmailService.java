package uz.akbar.resto.service;

import java.util.UUID;

/**
 * EmailService
 */
public interface EmailService {

	void sendRegistrationEmail(String email, UUID userId);
}
