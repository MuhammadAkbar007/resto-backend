package uz.akbar.resto.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.payload.AppResponse;
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
}
