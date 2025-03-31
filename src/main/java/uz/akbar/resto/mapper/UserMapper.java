package uz.akbar.resto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.payload.response.UserDetailsDto;
import uz.akbar.resto.payload.response.UserDto;

/**
 * UserMapper
 */
@Mapper(componentModel = "spring", uses = { RoleMapper.class, OrderMapper.class })
public interface UserMapper {

	@Mapping(target = "photoId", source = "photo", qualifiedByName = "attachmentToId")
	UserDto toUserDto(User user);

	@Mapping(target = "registrationDate", source = "createdAt")
	@Mapping(target = "photoId", source = "photo", qualifiedByName = "attachmentToId")
	UserDetailsDto toUserDetailsDto(User user);

	@Named("attachmentToId")
	default UUID attachmentToId(Attachment attachment) {
		return attachment.getId();
	}
}
