package uz.akbar.resto.payload.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {

	private String firstName;

	private String lastName;

	@Email(message = "Email should be valid")
	private String email;

	private String phoneNumber;

	private String password;
}
