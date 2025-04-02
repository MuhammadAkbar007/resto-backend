package uz.akbar.resto.mapper;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.payload.response.OrderDto;
import uz.akbar.resto.payload.response.RoleDto;
import uz.akbar.resto.payload.response.UserDetailsDto;
import uz.akbar.resto.payload.response.UserDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-02T10:43:54+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.photoId( attachmentToId( user.getPhoto() ) );
        userDto.id( user.getId() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.email( user.getEmail() );
        userDto.phoneNumber( user.getPhoneNumber() );
        userDto.status( user.getStatus() );
        userDto.roles( roleSetToRoleDtoSet( user.getRoles() ) );
        userDto.orders( orderSetToOrderDtoSet( user.getOrders() ) );

        return userDto.build();
    }

    @Override
    public UserDetailsDto toUserDetailsDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDetailsDto.UserDetailsDtoBuilder userDetailsDto = UserDetailsDto.builder();

        userDetailsDto.registrationDate( user.getCreatedAt() );
        userDetailsDto.photoId( attachmentToId( user.getPhoto() ) );
        userDetailsDto.id( user.getId() );
        userDetailsDto.firstName( user.getFirstName() );
        userDetailsDto.lastName( user.getLastName() );
        userDetailsDto.email( user.getEmail() );
        userDetailsDto.phoneNumber( user.getPhoneNumber() );
        userDetailsDto.status( user.getStatus() );
        userDetailsDto.visible( user.getVisible() );
        userDetailsDto.roles( roleSetToRoleDtoSet( user.getRoles() ) );
        userDetailsDto.orders( orderSetToOrderDtoSet( user.getOrders() ) );

        return userDetailsDto.build();
    }

    protected Set<RoleDto> roleSetToRoleDtoSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleDto> set1 = new LinkedHashSet<RoleDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleMapper.toDto( role ) );
        }

        return set1;
    }

    protected Set<OrderDto> orderSetToOrderDtoSet(Set<Order> set) {
        if ( set == null ) {
            return null;
        }

        Set<OrderDto> set1 = new LinkedHashSet<OrderDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Order order : set ) {
            set1.add( orderMapper.toDto( order ) );
        }

        return set1;
    }
}
