package uz.akbar.resto.payload.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.StorageType;

@Builder
@Getter
@Setter
public class AttachmentDto {

	private UUID id;

	private String originalName; /* pdp.jpg, inn.pdf */

	private Long size; /* 2048000 */

	private String contentType; /* application/pdf, image/png */

	private String extension; /* pdf, png */

	private String filePath; /* fs url (only for StorageType.FILE_SYSTEM) */

	private StorageType storageType; /* DATABASE, FILESYSTEM */
}
