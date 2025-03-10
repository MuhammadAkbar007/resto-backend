package uz.akbar.resto.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/** LogInDto */
@Getter
public class LogInDto {

	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	String email;

	@NotBlank(message = "Password is required")
	@Size(min = 5, message = "Password must be at least 5 characters long")
	String password;
}
