package uz.akbar.resto.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.security.CustomUserDetails;
import uz.akbar.resto.service.AdminService;
import uz.akbar.resto.utils.Utils;

@RestController
@RequestMapping(Utils.BASE_URL + "/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService service;

	/*
	 * /api/v1/admin/block/userId?status=BLOCK
	 * /api/v1/admin/block/userId?status=ACTIVE
	 */
	@PutMapping("/block/{userId}")
	public ResponseEntity<AppResponse> blockUnblockUser(@PathVariable UUID userId,
			@RequestParam(required = true) GeneralStatus status,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.blockUnblockUser(userId, status, customUserDetails.getUserId());
		return ResponseEntity.ok(response);
	}

	/*
	 * /api/v1/admin/assign/userId?roleType=ROLE_MANAGER
	 * /api/v1/admin/assign/userId?roleType=ROLE_EMPLOYEE
	 * /api/v1/admin/assign/userId?roleType=ROLE_ADMIN
	 */
	@PutMapping("/assign/{userId}")
	public ResponseEntity<AppResponse> assignRole(@PathVariable UUID userId,
			@RequestParam(required = true) RoleType roleType,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.assignRole(userId, roleType, customUserDetails.getUserId());
		return ResponseEntity.ok(response);
	}

	/*
	 * /api/v1/admin/revoke/userId?roleType=ROLE_MANAGER
	 * /api/v1/admin/revoke/userId?roleType=ROLE_EMPLOYEE
	 * /api/v1/admin/revoke/userId?roleType=ROLE_ADMIN
	 */
	@PutMapping("/revoke/{userId}")
	public ResponseEntity<AppResponse> revokeRole(@PathVariable UUID userId,
			@RequestParam(required = true) RoleType roleType,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		AppResponse response = service.revokeRole(userId, roleType, customUserDetails.getUserId());
		return ResponseEntity.ok(response);
	}
}
