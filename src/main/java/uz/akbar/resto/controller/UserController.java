package uz.akbar.resto.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.security.CustomUserDetails;
import uz.akbar.resto.service.UserService;
import uz.akbar.resto.utils.Utils;

@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/user")
public class UserController {

	private final UserService service;

	/**
	 * admin endpoint to get any user's info by id
	 * 
	 * @return ResponseEntity<?>
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUserById(@PathVariable UUID id) {
		AppResponse response = service.getUserById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
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
	 * @param fromDate
	 * @param toDate
	 * @param page
	 * @param size
	 * @param sort
	 * 
	 * @return AppResponse with users list
	 */
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUsers(
			@RequestParam(required = false) String searchTerm, // for general search
			@RequestParam(required = false) String firstName, // for filter
			@RequestParam(required = false) String lastName, // for filter
			@RequestParam(required = false) String email, // for filter
			@RequestParam(required = false) String phoneNumber, // for filter
			@RequestParam(required = false) GeneralStatus status, // for filter
			@RequestParam(required = false) RoleType role, // for filter
			@RequestParam(required = false) LocalDateTime fromDate, // for filter
			@RequestParam(required = false) LocalDateTime toDate, // for filter
			@RequestParam(defaultValue = "0") int page, // for pagination
			@RequestParam(defaultValue = "10") int size, // for pagination
			@RequestParam(defaultValue = "createdAt,desc") String[] sort) { // sorting for pagination

		AppResponse response = service.getUsers(searchTerm, firstName, lastName, email, phoneNumber, status, role,
				fromDate, toDate,
				page, size, sort);

		return ResponseEntity.ok(response);
	}
}
