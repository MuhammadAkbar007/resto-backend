package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.Role;
import uz.akbar.resto.payload.response.RoleDto;

/**
 * RoleMapper
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

	RoleDto toDto(Role role);
}
