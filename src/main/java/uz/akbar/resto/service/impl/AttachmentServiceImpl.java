package uz.akbar.resto.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.exception.FileDeletionException;
import uz.akbar.resto.exception.FileUploadException;
import uz.akbar.resto.repository.AttachmentRepository;
import uz.akbar.resto.service.AttachmentService;
import uz.akbar.resto.utils.Utils;

/**
 * AttachmentServiceImpl
 */
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

	private final AttachmentRepository repository;

	@Value("${app.image.compression-quality:0.7}")
	private float defaultCompressionQuality;

	@Value("${app.file.upload-dir:uploads}")
	private String uploadDir;

	@Value("${app.image.default-image-folder:static/images/}")
	private String defaultImageFolder;

	@Value("${app.image.default-profile-image-name:default-profile.png}")
	private String defaultProfileImageName;

	private String defaultProfileImagePath = defaultImageFolder + defaultProfileImageName;

	@Override
	public Attachment getDefaultProfileImage() {

		try {
			ClassPathResource imgFile = new ClassPathResource(defaultProfileImagePath);
			byte[] imgBytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

			Attachment defaultImage = Attachment.builder()
					.originalName("default-profile.png")
					.size((long) imgBytes.length)
					.contentType("image/png")
					.extension("png")
					.storageType(StorageType.FILE_SYSTEM)
					.filePath(defaultProfileImagePath)
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
	public void deleteAttachment(UUID attachmentId) {
		if (attachmentId != null) {
			Attachment attachment = repository.findById(attachmentId)
					.orElseThrow(() -> new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Attachment not found with id: " + attachmentId));

			if (defaultProfileImagePath.equals(attachment.getFilePath()))
				return;

			if (attachment.getCompressed() != null)
				deleteAttachment(attachment.getCompressed().getId());

			if (attachment.getStorageType() == StorageType.FILE_SYSTEM && attachment.getFilePath() != null) {
				Path path = Paths.get(uploadDir, attachment.getFilePath());

				try {
					Files.deleteIfExists(path);
				} catch (IOException e) {
					throw new FileDeletionException("Could not delete file: " + attachment.getFilePath(), e);
				}
			}

			repository.delete(attachment);
		}
	}

	@Override
	@Transactional
	public Attachment saveAttachment(MultipartFile file, StorageType storageType) {
		if (file.isEmpty())
			throw new FileUploadException(HttpStatus.BAD_REQUEST, "Cannot process an empty file");

		String rawFilename = file.getOriginalFilename();
		if (!StringUtils.hasText(rawFilename))
			throw new FileUploadException(HttpStatus.BAD_REQUEST, "File must have a name");

		String originalFilename = StringUtils.cleanPath(rawFilename);
		String extension = getFileExtension(originalFilename);
		String key = UUID.randomUUID().toString();
		String filename = key + "." + extension;

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
				throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file to database",
						e);
			}
		} else {
			String pathFolder = getYearMonthDay();
			String fullPath = uploadDir + File.separator + pathFolder;

			File folder = new File(fullPath);
			if (!folder.exists()) {
				boolean hasCreated = folder.mkdirs();
				if (!hasCreated)
					throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Failed to create directory: " + fullPath);
			}

			String filePath = pathFolder + File.separator + filename;
			Path targetLocation = Paths.get(uploadDir, filePath);

			try {
				Files.write(targetLocation, file.getBytes());
			} catch (IOException e) {
				throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write file: " + filePath);
			}

			attachment.setFilePath(filePath);
		}

		Attachment savedAttachment = repository.save(attachment);

		// compress if it is an image
		if (savedAttachment.getContentType() != null && savedAttachment.getContentType().startsWith("image/")) {
			try {
				Attachment compressed = compressImage(savedAttachment, defaultCompressionQuality);
				savedAttachment.setCompressed(compressed);
				savedAttachment = repository.save(savedAttachment);
			} catch (IOException e) {
				throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed while compressing" + e);
			}
		}

		return savedAttachment;
	}

	@Transactional
	private Attachment compressImage(Attachment original, float quality) throws IOException {
		String extension = original.getExtension().toLowerCase();
		if (!isCompressableImageFormat(extension))
			throw new FileUploadException(HttpStatus.BAD_REQUEST, "Unsupported format for compression: " + extension);

		BufferedImage originalImage;
		if (original.getStorageType() == StorageType.DATABASE) {
			originalImage = ImageIO.read(new ByteArrayInputStream(original.getContent()));
		} else {
			Path fullPath = Paths.get(uploadDir, original.getFilePath());
			originalImage = ImageIO.read(fullPath.toFile());
		}

		if (originalImage == null)
			throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Failed to read image for compression (id): " + original.getId());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		if (extension.equals("jpg") || extension.equals("jpeg")) {
			// For JPEG, we can control compression quality
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);

			try (ImageOutputStream ios = new MemoryCacheImageOutputStream(outputStream)) {
				writer.setOutput(ios);
				writer.write(null, new IIOImage(originalImage, null, null), param);
				writer.dispose();
			} catch (Exception e) {
				throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed during compression" + e);
			}
		} else {
			// For other formats like PNG, GIF, etc.
			ImageIO.write(originalImage, extension, outputStream);
		}

		byte[] compressedBytes = outputStream.toByteArray();
		String key = UUID.randomUUID().toString();
		String compressedFileName = key + "_compressed." + extension;

		Attachment compressed = Attachment.builder()
				.originalName("compressed_" + original.getOriginalName())
				.size((long) compressedBytes.length)
				.contentType(original.getContentType())
				.extension(extension)
				.storageType(original.getStorageType())
				.visible(true)
				.createdAt(Instant.now())
				.build();

		if (original.getStorageType() == StorageType.DATABASE) {
			compressed.setContent(compressedBytes);
		} else {
			String pathFolder = getYearMonthDay();
			String fullPath = uploadDir + File.separator + pathFolder;

			File folder = new File(fullPath);
			if (!folder.exists()) {
				boolean hasCreated = folder.mkdirs();
				if (!hasCreated)
					throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Failed to create directory: " + fullPath);
			}

			String filePath = pathFolder + File.separator + compressedFileName;
			Path targetLocation = Paths.get(uploadDir, filePath);
			Files.write(targetLocation, compressedBytes);

			compressed.setFilePath(filePath);
		}

		return repository.save(compressed);
	}

	/**
	 * Checks if the given format can be compressed
	 * 
	 * @param extension the file extension
	 * @return true if the format is compressible
	 */
	private boolean isCompressableImageFormat(String extension) {
		return extension.equals("jpg") || extension.equals("jpeg") ||
				extension.equals("png") || extension.equals("gif") ||
				extension.equals("bmp");
	}

	/**
	 * Gets the current date in "yyyy/MM/dd" format for directory organization
	 * 
	 * @return String with year/month/day format
	 */
	private String getYearMonthDay() {
		return LocalDate.now()
				.format(DateTimeFormatter.ofPattern(Utils.DATE_TIME_PATTERN));
	}

	/**
	 * Gets the file extension from a filename
	 * 
	 * @param filename the filename
	 * @return the extension without the dot
	 */
	private String getFileExtension(String filename) {
		if (filename == null || filename.lastIndexOf(".") == -1)
			return "";

		return filename.substring(filename.lastIndexOf(".") + 1);
	}

}
