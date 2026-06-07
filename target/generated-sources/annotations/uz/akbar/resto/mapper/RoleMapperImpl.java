package uz.akbar.resto.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.payload.response.RoleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-10T16:17:59+0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        roleDto.description( role.getDescription() );
        roleDto.id( role.getId() );
        roleDto.roleType( role.getRoleType() );

        return roleDto.build();
    }
}
