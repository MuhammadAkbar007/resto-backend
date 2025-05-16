package uz.akbar.resto.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.payload.AppResponse;

/**
 * AttachmentService
 */
public interface AttachmentService {

	Attachment getDefaultProfileImage();

	void deleteHardAttachment(UUID attachmentId);

	void deleteSoftAttachment(UUID attachmentId);

	Attachment findAttachmentById(UUID id);

	Attachment saveAttachment(MultipartFile photo, StorageType fileSystem);

	AppResponse createAttachment(MultipartFile file, StorageType fileSystem);

	ResponseEntity<Resource> openAttachment(UUID id);

	ResponseEntity<Resource> downloadAttachment(UUID id);

	ResponseEntity<Resource> openThumbnail(UUID idOfOriginal);
}
