package uz.akbar.resto.payload.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.akbar.resto.enums.GeneralStatus;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

	private UUID id;

	private String firstName;

	private String lastName;

	private String email;

	private String phoneNumber;

	private GeneralStatus status;

	private UUID photoId;

	private Instant registrationDate;

	private Boolean visible;

	private Set<RoleDto> roles;

	private Set<OrderDto> orders;
}
