package uz.akbar.resto.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.RoleType;

/**
 * RoleDto
 */
@Builder
@Getter
@Setter
public class RoleDto {

	private Long id;

	private RoleType roleType;
}
