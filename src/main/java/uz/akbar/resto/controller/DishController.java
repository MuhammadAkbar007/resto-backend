package uz.akbar.resto.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.DishCategory;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;
import uz.akbar.resto.payload.request.UpdateDishDto;
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
	@PostMapping
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
	public ResponseEntity<AppResponse> create(@Valid @RequestBody DishDto dto) {
		AppResponse response = service.create(dto);
		return ResponseEntity.status(201).body(response);
	}

	@GetMapping("/all")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getAll(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) String name, // for filter
			@RequestParam(required = false) String price, // for filter
			@RequestParam(required = false) String quantityAvailable, // for filter
			@RequestParam(required = false) DishCategory dishCategory, // for filter
			@RequestParam(required = false) GeneralStatus status, // for filter
			@RequestParam(required = false) LocalDateTime fromDateTime, // for filter
			@RequestParam(required = false) LocalDateTime toDateTime, // for filter
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) {

		AppResponse response = service.getAll(searchTerm, name, price, quantityAvailable, dishCategory, status,
				fromDateTime, toDateTime, page, size, sort);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getById(@PathVariable Long id) {
		AppResponse response = service.getById(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
	public ResponseEntity<AppResponse> edit(@PathVariable Long id, @RequestBody UpdateDishDto dto) {
		AppResponse response = service.edit(id, dto);
		return ResponseEntity.ok(response);
	}

}
