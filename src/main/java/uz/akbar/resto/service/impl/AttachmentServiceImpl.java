package uz.akbar.resto.service.impl;

import java.io.IOException;
import java.time.Instant;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.service.AttachmentService;

/**
 * AttachmentServiceImpl
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

	public static final String filePath = "static/images/default-profile.png";

	@Override
	public Attachment getDefaultProfileImage() {

		try {
			ClassPathResource imgFile = new ClassPathResource(filePath);
			byte[] imgBytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

			Attachment defaultImage = Attachment.builder()
					.originalName("default-profile.png")
					.size((long) imgBytes.length)
					.contentType("image/png")
					.extension("png")
					.storageType(StorageType.FILE_SYSTEM)
					.filePath(filePath)
					.content(null)
					.visible(true)
					.createdAt(Instant.now())
					.build();

			return defaultImage;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load default user image", e);
		}

	}

}
