package uz.akbar.resto.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.exception.FileDeletionException;
import uz.akbar.resto.exception.FileUploadException;
import uz.akbar.resto.repository.AttachmentRepository;
import uz.akbar.resto.service.AttachmentService;

/**
 * AttachmentServiceImpl
 */
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

	private final AttachmentRepository repository;

	@Value("${app.file.upload-dir:uploads}")
	private String uploadDir;

	@Value("${app.image.compression-quality:0.7}")
	private float defaultCompressionQuality;

	public static final String DEFAULT_IMAGE_PATH = "static/images/default-profile.png";

	@Override
	public Attachment getDefaultProfileImage() {

		try {
			ClassPathResource imgFile = new ClassPathResource(DEFAULT_IMAGE_PATH);
			byte[] imgBytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

			Attachment defaultImage = Attachment.builder()
					.originalName("default-profile.png")
					.size((long) imgBytes.length)
					.contentType("image/png")
					.extension("png")
					.storageType(StorageType.FILE_SYSTEM)
					.filePath(DEFAULT_IMAGE_PATH)
					.content(null)
					.visible(true)
					.createdAt(Instant.now())
					.build();

			return defaultImage;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load default user image", e);
		}

	}

	@Override
	@Transactional
	public void deleteAttachment(Attachment photo) {
		if (photo != null) {
			if (DEFAULT_IMAGE_PATH.equals(photo.getFilePath()))
				return;

			if (photo.getCompressed() != null)
				deleteAttachment(photo.getCompressed());

			if (photo.getStorageType() == StorageType.FILE_SYSTEM && photo.getFilePath() != null) {
				Path path = Paths.get(photo.getFilePath());

				try {
					Files.deleteIfExists(path);
				} catch (IOException e) {
					throw new FileDeletionException("Could not delete file: " + photo.getFilePath(), e);
				}
			}

			repository.delete(photo);
		}
	}

	@Override
	public Attachment saveAttachment(MultipartFile file, StorageType storageType) {
		if (file.isEmpty())
			throw new AppBadRequestException("Failed to store an empty file");

		String rawFilename = file.getOriginalFilename();
		if (!StringUtils.hasText(rawFilename))
			// TODO: consider giving status code for exception constructor
			throw new FileUploadException("Uploaded file is empty");

		String originalFilename = StringUtils.cleanPath(rawFilename);
		String extension = getFileExtension(originalFilename);
		String key = UUID.randomUUID().toString();
		String filename = key + "." + extension;

		// TODO: filePath & compressed
		Attachment attachment = Attachment.builder()
				.originalName(originalFilename)
				.size(file.getSize())
				.contentType(file.getContentType())
				.extension(extension)
				.storageType(storageType)
				.visible(true)
				.createdAt(Instant.now())
				.build();

		if (storageType == StorageType.DATABASE) {
			try {
				attachment.setContent(file.getBytes());
			} catch (IOException e) {
				// TODO: throw FileUploadException
				e.printStackTrace();
			}
		} else {
			// TODO: save to FS logic
		}

		return null;
	}

	/**
	 * Gets the file extension from a filename
	 * 
	 * @param filename the filename
	 * @return the extension without the dot
	 */
	private String getFileExtension(String filename) {
		if (filename == null || filename.lastIndexOf(".") == -1) {
			return "";
		}
		return filename.substring(filename.lastIndexOf(".") + 1);
	}

}
