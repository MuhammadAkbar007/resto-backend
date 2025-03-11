package uz.akbar.resto.payload.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/** RegisterDto */
@Getter
public class RegisterDto {

	@NotBlank(message = "First name is required")
	private String firstName;

	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

	@NotBlank(message = "Phone number is required without + e.g, 998945060749")
	@Size(min = 12, max = 12, message = "Phone number must be exactly 12 digits")
	@Digits(integer = 12, fraction = 0, message = "Phone number must contain only digits")
	private String phoneNumber;

	@NotBlank(message = "Password is required")
	@Size(min = 5, message = "Password must be at least 5 characters long")
	private String password;
}
