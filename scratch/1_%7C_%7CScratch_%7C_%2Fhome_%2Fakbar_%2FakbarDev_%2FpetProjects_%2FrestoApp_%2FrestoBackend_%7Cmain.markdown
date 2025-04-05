# Clude AttachmentServiceImpl

```java
package uz.akbar.resto.service.impl;

import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.Objects;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.repository.AttachmentRepository;
import uz.akbar.resto.service.AttachmentService;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    
    private final AttachmentRepository attachmentRepository;
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    // Default compression settings for profile photos
    @Value("${app.image.max-width:800}")
    private int defaultMaxWidth;
    
    @Value("${app.image.max-height:800}")
    private int defaultMaxHeight;
    
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
    public Attachment saveAttachment(MultipartFile file, StorageType storageType) throws IOException {
        // Use the default compression settings for images
        return saveAttachment(file, storageType, true, defaultMaxWidth, defaultMaxHeight, defaultCompressionQuality);
    }
    
    @Override
    public Attachment saveAttachment(MultipartFile file, StorageType storageType, boolean compress, 
                                   int maxWidth, int maxHeight, float compressionQuality) throws IOException {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new AppBadRequestException("Failed to store empty file");
        }
        
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFilename);
        
        // Generate unique ID for the file
        String key = UUID.randomUUID().toString();
        String filename = key + "." + extension;
        
        // Build the attachment entity
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
            // Store the file content in the database
            attachment.setContent(file.getBytes());
        } else {
            // Create date-based folder structure (yyyy/MM/dd)
            String pathFolder = getYearMonthDayString();
            String fullPath = uploadDir + File.separator + pathFolder;
            
            // Create folders if they don't exist
            File folder = new File(fullPath);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + fullPath);
                }
            }
            
            // Full path to the file
            String filePath = pathFolder + File.separator + filename;
            Path targetLocation = Paths.get(uploadDir, filePath);
            
            // Save file to the file system
            Files.write(targetLocation, file.getBytes());
            
            // Set the relative path in the attachment entity
            attachment.setFilePath(uploadDir + File.separator + filePath);
        }
        
        // Save the attachment in the database
        Attachment savedAttachment = attachmentRepository.save(attachment);
        
        // Create a compressed version if it's an image and compression is requested
        if (compress && savedAttachment.getContentType() != null && 
            savedAttachment.getContentType().startsWith("image/")) {
            try {
                Attachment compressed = compressImage(savedAttachment, maxWidth, maxHeight, compressionQuality);
                savedAttachment.setCompressed(compressed);
                savedAttachment = attachmentRepository.save(savedAttachment);
            } catch (Exception e) {
                // Log the error but continue with the uncompressed version
                System.err.println("Failed to create compressed version: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return savedAttachment;
    }
    
    @Override
    public Attachment compressImage(Attachment original, int maxWidth, int maxHeight, float quality) throws IOException {
        String extension = original.getExtension().toLowerCase();
        
        // Make sure the format is supported for compression
        if (!isCompressibleImageFormat(extension)) {
            throw new IOException("Unsupported image format for compression: " + extension);
        }
        
        // Read the original image
        BufferedImage originalImage;
        if (original.getStorageType() == StorageType.DATABASE) {
            originalImage = ImageIO.read(new ByteArrayInputStream(original.getContent()));
        } else {
            originalImage = ImageIO.read(new File(original.getFilePath()));
        }
        
        if (originalImage == null) {
            throw new IOException("Failed to read image for compression");
        }
        
        // Calculate new dimensions while preserving aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Only resize if the image is larger than the max dimensions
        int targetWidth = originalWidth;
        int targetHeight = originalHeight;
        
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            targetWidth = (int) (originalWidth * ratio);
            targetHeight = (int) (originalHeight * ratio);
        }
        
        // If the image is already smaller than the max dimensions, no need to resize
        boolean resizeNeeded = targetWidth != originalWidth || targetHeight != originalHeight;
        
        // Skip compression if the image is already small and quality is high
        if (!resizeNeeded && quality >= 0.95) {
            return original;  // Just return the original
        }
        
        // Create resized image if needed
        BufferedImage resizedImage = originalImage;
        if (resizeNeeded) {
            resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();
        }
        
        // Compress the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            // For JPEG, we can control compression quality
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // 0.0-1.0, lower means more compression
            
            try (ImageOutputStream ios = new MemoryCacheImageOutputStream(outputStream)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(resizedImage, null, null), param);
                writer.dispose();
            }
        } else {
            // For other formats like PNG, GIF, etc.
            ImageIO.write(resizedImage, extension, outputStream);
        }
        
        byte[] compressedBytes = outputStream.toByteArray();
        
        // Generate a new UUID for the compressed file
        String key = UUID.randomUUID().toString();
        String compressedFilename = key + "_compressed." + extension;
        
        // Create the compressed attachment
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
            // Create date-based folder structure for compressed image
            String pathFolder = getYearMonthDayString();
            String fullPath = uploadDir + File.separator + pathFolder;
            
            // Create folders if they don't exist
            File folder = new File(fullPath);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory for compressed image: " + fullPath);
                }
            }
            
            // Full path to the compressed file
            String filePath = pathFolder + File.separator + compressedFilename;
            Path targetLocation = Paths.get(uploadDir, filePath);
            
            // Save compressed file to the file system
            Files.write(targetLocation, compressedBytes);
            
            // Set the relative path in the compressed attachment entity
            compressed.setFilePath(uploadDir + File.separator + filePath);
        }
        
        // Save the compressed attachment
        return attachmentRepository.save(compressed);
    }
    
    @Override
    public void deleteAttachment(Attachment attachment) {
        if (attachment != null) {
            // Don't delete the default profile image
            if (DEFAULT_IMAGE_PATH.equals(attachment.getFilePath())) {
                return;
            }
            
            // If there's a compressed version, delete it first
            if (attachment.getCompressed() != null) {
                deleteAttachment(attachment.getCompressed());
            }
            
            if (attachment.getStorageType() == StorageType.FILE_SYSTEM && 
                attachment.getFilePath() != null && 
                !attachment.getFilePath().equals(DEFAULT_IMAGE_PATH)) {
                try {
                    Path filePath = Paths.get(attachment.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    // Log the exception
                    System.err.println("Error deleting file: " + e.getMessage());
                }
            }
            
            attachmentRepository.delete(attachment);
        }
    }
    
    /**
     * Gets the file extension from a filename
     * @param filename the filename
     * @return the extension without the dot
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * Gets the current date in "yyyy/MM/dd" format for directory organization
     * @return String with year/month/day format
     */
    private String getYearMonthDayString() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return now.format(formatter);
    }
    
    /**
     * Checks if the given format can be compressed
     * @param extension the file extension
     * @return true if the format is compressible
     */
    private boolean isCompressibleImageFormat(String extension) {
        // Common image formats that can be compressed
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif") || 
               extension.equals("bmp");
    }
}
```
