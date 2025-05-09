package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.payload.response.AttachmentDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttachmentMapper {

	AttachmentDto toDto(Attachment attachment);
}
