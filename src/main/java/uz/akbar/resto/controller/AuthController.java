package uz.akbar.resto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.LogInDto;
import uz.akbar.resto.payload.request.OtpVerificationDto;
import uz.akbar.resto.payload.request.RefreshTokenRequestDto;
import uz.akbar.resto.payload.request.RegisterDto;
import uz.akbar.resto.service.AuthService;
import uz.akbar.resto.utils.Utils;

/** AuthController */
@RequiredArgsConstructor
@RestController
@RequestMapping(Utils.BASE_URL + "/auth")
public class AuthController {

	private final AuthService service;

	@PostMapping("/register")
	public ResponseEntity<AppResponse> registerUser(@Valid @RequestBody RegisterDto dto) {
		AppResponse response = service.registerUser(dto);
		return ResponseEntity.status(201).body(response);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<AppResponse> verifyOtp(@Valid @RequestBody OtpVerificationDto dto) {
		AppResponse response = service.verifyOtp(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AppResponse> loginToSystem(@Valid @RequestBody LogInDto dto) {
		AppResponse response = service.logIn(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AppResponse> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dto) {
		AppResponse response = service.refreshToken(dto);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<AppResponse> logout(@Valid @RequestBody RefreshTokenRequestDto dto) {
		service.logout(dto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
