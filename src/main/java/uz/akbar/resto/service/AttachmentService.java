package uz.akbar.resto.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;

/**
 * AttachmentService
 */
public interface AttachmentService {

	Attachment getDefaultProfileImage();

	void deleteAttachment(UUID attachmentI);

	Attachment saveAttachment(MultipartFile photo, StorageType fileSystem);
}
