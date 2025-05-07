package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.payload.response.AttachmentDto;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

	AttachmentDto toDto(Attachment attachment);
}
