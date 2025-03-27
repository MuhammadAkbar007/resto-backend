package uz.akbar.resto.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.payload.response.RoleDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-28T02:23:50+0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.41.0.z20250115-2156, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        roleDto.id( role.getId() );
        roleDto.roleType( role.getRoleType() );

        return roleDto.build();
    }
}
