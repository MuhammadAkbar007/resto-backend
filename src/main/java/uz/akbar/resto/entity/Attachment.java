package uz.akbar.resto.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uz.akbar.resto.entity.template.AbsUUIDEntity;
import uz.akbar.resto.enums.StorageType;

/** Attachment */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
public class Attachment extends AbsUUIDEntity {

	@Column(nullable = false)
	private String originalName; /* pdp.jpg, inn.pdf */

	@Column(nullable = false)
	private Long size; /* 2048000 */

	@Column(nullable = false)
	private String contentType; /* application/pdf, image/png */

	@Column(nullable = false)
	private String extension; /* pdf, png */

	// @Column(unique = true)
	private String filePath; /* fs url (only for StorageType.FILE_SYSTEM) */

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StorageType storageType; /* DATABASE, FILESYSTEM */

	// @Lob
	@Column(columnDefinition = "BYTEA")
	private byte[] content; /* byte version in db table (only for StorageType.DATABASE) */

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Attachment compressed; /* compressed version of pictures */
}
