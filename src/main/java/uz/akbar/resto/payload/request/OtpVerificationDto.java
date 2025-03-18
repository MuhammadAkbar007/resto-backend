package uz.akbar.resto.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
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

	@NotBlank
	private UUID userId;

	@NotBlank
	private String otp;
}
