package uz.akbar.resto.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.UpdateUserRequestDto;
import uz.akbar.resto.security.CustomUserDetails;
import uz.akbar.resto.service.UserService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/user")
public class UserController {

	private final UserService service;

	/**
	 * Update a user's profile photo.
	 * If userId is not provided, updates the current authenticated user's photo.
	 * If userId is provided, only admins can update other users' photos.
	 */
	@PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> updateUserPhoto(
			@RequestParam(required = false) UUID userId,
			@RequestParam MultipartFile photo,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.updateUserPhoto(userId, photo, customUserDetails.getUser());
		return ResponseEntity.ok(response);
	}

	/**
	 * admin endpoint to get any user's info by id
	 * 
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AppResponse> getUserById(@PathVariable UUID id) {
		AppResponse response = service.getUserById(id);
		return ResponseEntity.ok(response);
	}

	/**
	 * get user his/her information
	 * 
	 * @param customUserDetails
	 * @return
	 */
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		AppResponse response = service.getCurrentUser(customUserDetails.getUserId());
		return ResponseEntity.ok(response);
	}

	/**
	 * For ADMIN
	 * Get users with pagination and sorting
	 * filter and search
	 * 
	 * @param searchTerm
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param status
	 * @param role
	 * @param fromDateTime
	 * @param toDateTime
	 * @param page
	 * @param size
	 * @param sort
	 * 
	 * @return AppResponse with users list
	 */
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AppResponse> getUsers(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) String firstName, // for filter
			@RequestParam(required = false) String lastName, // for filter
			@RequestParam(required = false) String email, // for filter
			@RequestParam(required = false) String phoneNumber, // for filter
			@RequestParam(required = false) GeneralStatus status, // for filter
			@RequestParam(required = false) RoleType role, // for filter
			@RequestParam(required = false) LocalDateTime fromDateTime, // for filter
			@RequestParam(required = false) LocalDateTime toDateTime, // for filter
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) { // sorting for pagination

		AppResponse response = service.getAllUsers(searchTerm, firstName, lastName, email, phoneNumber, status, role,
				fromDateTime, toDateTime, page, size, sort);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequestDto dto,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.update(id, dto, customUserDetails.getUser());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AppResponse> deleteSoft(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		service.delete(id, customUserDetails.getUser());
		return ResponseEntity.noContent().build();
	}
}
