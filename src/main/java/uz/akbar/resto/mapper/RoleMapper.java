package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;

import uz.akbar.resto.entity.Role;
import uz.akbar.resto.payload.response.RoleDto;

/**
 * RoleMapper
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

	RoleDto toDto(Role role);
}
