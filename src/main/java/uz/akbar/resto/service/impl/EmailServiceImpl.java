package uz.akbar.resto.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.service.EmailService;

/**
 * EmailServiceImpl
 */
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromAccount;

	private void sendMimeMessage(String email, String subject, String body) {
		try {
			MimeMessage msg = javaMailSender.createMimeMessage();
			msg.setFrom(fromAccount);

			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(body, true);
			javaMailSender.send(msg);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendOtpEmail(String email, String otp, int expiryMinutes) {
		String subject = "Your OTP for Registration";
		String body = "<!doctype html>\n"
				+ "<html lang=\"en\">\n"
				+ "  <head>\n"
				+ "    <meta charset=\"UTF-8\" />\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\""
				+ " />\n"
				+ "    <title>Registration OTP</title>\n"
				+ "    <style>\n"
				+ "      .otp-container {\n"
				+ "        padding: 20px;\n"
				+ "        background-color: #f5f5f5;\n"
				+ "        border-radius: 5px;\n"
				+ "        font-size: 24px;\n"
				+ "        letter-spacing: 5px;\n"
				+ "        text-align: center;\n"
				+ "        font-weight: bold;\n"
				+ "        margin: 20px 0;\n"
				+ "      }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <h1>Complete Registration</h1>\n"
				+ "    <p>Assalomu alaykum!</p>\n"
				+ "    <p>Please use the following OTP code to complete your registration:</p>\n"
				+ "    <div class=\"otp-container\">\n"
				+ "      %s\n"
				+ "    </div>\n"
				+ "    <p>This code will expire in %d minutes.</p>\n"
				+ "    <p>If you did not request this code, please ignore this email.</p>\n"
				+ "  </body>\n"
				+ "</html>";
		body = String.format(body, otp, expiryMinutes);
		sendMimeMessage(email, subject, body);
	}

	@Override
	public void sendRegistrationEmail(String email, UUID userId) {
		String subject = "Complete registration";

		String body = "<!doctype html>\n"
				+ "<html lang=\"en\">\n"
				+ "  <head>\n"
				+ "    <meta charset=\"UTF-8\" />\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\""
				+ " />\n"
				+ "    <title>Email Verification</title>\n"
				+ "    <style>\n"
				+ "      .button-link {\n"
				+ "        padding: 10px 30px;\n"
				+ "        display: inline-block;\n"
				+ "        text-decoration: none;\n"
				+ "        color: white;\n"
				+ "        background-color: #ffd700;\n"
				+ "      }\n"
				+ "\n"
				+ "      .button-link:hover {\n"
				+ "        background-color: #9c840b;\n"
				+ "      }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <h1>Complete Registration</h1>\n"
				+ "    <p>Assalomu alaykum!</p>\n"
				+ "    <p>Please, click the following button to complete registration 👇</p>\n"
				+ "    <a\n"
				+ "      target=\"_blank\"\n"
				+ "      href=\"http://localhost:8080/auth/registration/verification/%s\"\n"
				+ "      class=\"button-link\"\n"
				+ "    >\n"
				+ "      Click Me\n"
				+ "    </a>\n"
				+ "  </body>\n"
				+ "</html>";

		body = String.format(body, userId);

		sendMimeMessage(email, subject, body);
	}

}
