package uz.akbar.resto.payload.response;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.GeneralStatus;

/**
 * UserDto
 */
@Builder()
@Getter
@Setter
public class UserDto {

	private UUID id;

	private String firstName;

	private String lastName;

	private GeneralStatus status;

	private UUID photoId;

	private Set<RoleDto> roles;

	@Builder.Default
	private Set<OrderDto> orders = new HashSet<>();
}
