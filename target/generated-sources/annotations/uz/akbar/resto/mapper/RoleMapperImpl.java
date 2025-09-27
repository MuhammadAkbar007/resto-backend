package uz.akbar.resto.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.payload.response.RoleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-20T09:45:13+0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.50.v20250731-0946, environment: Java 24.0.2 (Oracle Corporation)"
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
