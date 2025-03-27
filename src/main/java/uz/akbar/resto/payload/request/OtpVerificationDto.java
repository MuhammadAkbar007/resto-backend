package uz.akbar.resto.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OtpVerificationDto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationDto {

	@NotNull(message = "user id is required")
	private UUID userId;

	@NotBlank(message = "otp is required")
	private String otp;
}
