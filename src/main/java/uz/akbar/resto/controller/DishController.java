package uz.akbar.resto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;
import uz.akbar.resto.service.DishService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/dish")
public class DishController {

	private final DishService service;

	/**
	 * client sends 1st request to save attachment
	 * client gets attachmentId from response
	 * client sends 2nd request with metadata + attachmentId
	 */
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
	@PostMapping
	public ResponseEntity<AppResponse> create(@Valid @RequestBody DishDto dto) {
		AppResponse response = service.create(dto);
		return ResponseEntity.status(201).body(response);
	}
}
