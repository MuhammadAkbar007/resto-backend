package uz.akbar.resto.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.service.AttachmentService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/attach")
public class AttachmentController {

	private final AttachmentService service;

	@PostMapping("/upload")
	public ResponseEntity<AppResponse> create(@RequestParam("file") MultipartFile file) {
		AppResponse response = service.createAttachment(file, StorageType.FILE_SYSTEM);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/open/{id}")
	public ResponseEntity<Resource> open(@PathVariable UUID id) {
		return service.openAttachment(id);
	}

	@GetMapping("/open/{id}/thumbnail")
	public ResponseEntity<Resource> openThumbnail(@PathVariable UUID id) {
		return service.openThumbnail(id);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> download(@PathVariable UUID id) {
		return service.downloadAttachment(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AppResponse> delete(@PathVariable UUID id) {
		service.deleteHardAttachment(id);
		return ResponseEntity.noContent().build();
	}
}
