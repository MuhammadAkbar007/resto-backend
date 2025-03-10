package uz.akbar.resto.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** RefreshTokenRequestDto */
@Getter
public class RefreshTokenRequestDto {

	@NotBlank(message = "token is required")
	String refreshToken;
}
