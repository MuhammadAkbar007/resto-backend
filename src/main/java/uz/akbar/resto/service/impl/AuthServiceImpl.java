package uz.akbar.resto.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.UserMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.LogInDto;
import uz.akbar.resto.payload.request.OtpVerificationDto;
import uz.akbar.resto.payload.request.RefreshTokenRequestDto;
import uz.akbar.resto.payload.request.RegisterDto;
import uz.akbar.resto.payload.response.UserDto;
import uz.akbar.resto.repository.RoleRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.AttachmentService;
import uz.akbar.resto.service.AuthService;
import uz.akbar.resto.service.EmailService;

/** AuthServiceImpl */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final UserMapper userMapper;
	private final AttachmentService attachmentService;
	private final OtpVerificationServiceImpl otpVerificationService;
	// private final AuthenticationManager authenticationManager;
	// private final JwtProvider jwtProvider;
	// private final RefreshTokenRepository refreshTokenRepository;

	@Override
	@Transactional
	public AppResponse registerUser(RegisterDto dto) {

		Optional<User> optional = repository.findByEmailOrPhoneNumberAndVisibleTrue(dto.getEmail(),
				dto.getPhoneNumber());

		optional.ifPresent(user -> {
			if (user.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
				repository.delete(user);
				repository.flush();
			} else {
				throw new AppBadRequestException("User already exists");
			}
		});

		Role roleCustomer = roleRepository
				.findByRoleType(RoleType.CUSTOMER)
				.orElseThrow(() -> new RuntimeException("Role Customer is not found"));

		User user = User.builder()
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.email(dto.getEmail())
				.phoneNumber(dto.getPhoneNumber())
				.password(passwordEncoder.encode(dto.getPassword()))
				.status(GeneralStatus.IN_REGISTRATION)
				.photo(attachmentService.getDefaultProfileImage())
				.roles(Set.of(roleCustomer))
				.visible(true)
				.createdAt(Instant.now())
				.build();

		User saved = repository.save(user);

		String otp = otpVerificationService.createOtp(saved.getId());
		// emailService.sendRegistrationEmail(saved.getEmail(), saved.getId());
		emailService.sendOtpEmail(saved.getEmail(), otp, otpVerificationService.getExpiryMinutes());

		UserDto userDto = userMapper.toDto(saved);

		return AppResponse.builder()
				.success(true)
				.message("Validation token has been sent to your email for register verification")
				.data(userDto)
				.build();
	}

	@Override
	public AppResponse logIn(LogInDto dto) {
		// Authentication authentication = authenticationManager.authenticate(
		// new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
		//
		// SecurityContextHolder.getContext().setAuthentication(authentication);
		//
		// CustomUserDetails userDetails = (CustomUserDetails)
		// authentication.getPrincipal();
		// User user = userDetails.getUser();
		//
		// JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		// JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);
		//
		// JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
		// .username(user.getUsername())
		// .accessToken(accessTokenDto.token())
		// .refreshToken(refreshTokenDto.token())
		// .accessTokenExpiryTime(accessTokenDto.expiryDate())
		// .refreshTokenExpiryTime(refreshTokenDto.expiryDate())
		// .build();
		//
		// return AppResponse.builder()
		// .success(true)
		// .message("User successfully logged in")
		// .data(jwtResponseDto)
		// .build();
		return null;
	}

	@Override
	@Transactional
	public AppResponse refreshToken(RefreshTokenRequestDto dto) {
		// String refreshToken = dto.refreshToken();
		//
		// if (!jwtProvider.validateToken(refreshToken)) {
		// throw new RefreshTokenException("Invalid refresh token");
		// }
		//
		// RefreshToken storedToken = refreshTokenRepository
		// .findByToken(refreshToken)
		// .orElseThrow(
		// () -> new RefreshTokenException(
		// "Refresh token is not in database!"));
		//
		// if (storedToken.getExpiryDate().isBefore(Instant.now())) {
		// refreshTokenRepository.delete(storedToken);
		// throw new RefreshTokenException("Refresh token has expired");
		// }
		//
		// String username = jwtProvider.getUsernameFromToken(refreshToken);
		//
		// CustomUserDetails userDetails = (CustomUserDetails)
		// SecurityContextHolder.getContext().getAuthentication()
		// .getPrincipal();
		//
		// UsernamePasswordAuthenticationToken authentication = new
		// UsernamePasswordAuthenticationToken(
		// userDetails, null, userDetails.getAuthorities());
		//
		// JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		// JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);
		//
		// JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
		// .username(username)
		// .accessToken(accessTokenDto.token())
		// .refreshToken(refreshTokenDto.token())
		// .accessTokenExpiryTime(accessTokenDto.expiryDate())
		// .refreshTokenExpiryTime(refreshTokenDto.expiryDate())
		// .build();
		//
		// return AppResponse.builder()
		// .success(true)
		// .message("Tokens successfully regenerated")
		// .data(jwtResponseDto)
		// .build();
		return null;
	}

	@Override
	@Transactional
	public void logout(RefreshTokenRequestDto dto) {
		// String refreshToken = dto.refreshToken();
		//
		// if (!jwtProvider.validateToken(refreshToken)) {
		// throw new RefreshTokenException("Invalid refresh token");
		// }
		//
		// RefreshToken storedToken = refreshTokenRepository
		// .findByToken(refreshToken)
		// .orElseThrow(
		// () -> new RefreshTokenException(
		// "Refresh token is not in database!"));
		//
		// if (storedToken.getExpiryDate().isBefore(Instant.now())) {
		// refreshTokenRepository.delete(storedToken);
		// throw new RefreshTokenException("Refresh token has expired");
		// }
		//
		// refreshTokenRepository.delete(storedToken);
		// SecurityContextHolder.clearContext();
	}

	@Override
	public AppResponse verifyOtp(OtpVerificationDto dto) {
		boolean isValid = otpVerificationService.verifyOtp(dto.getOtp(), dto.getUserId());

		if (!isValid)
			throw new AppBadRequestException("Invalid or expired OTP");

		User user = repository.findById(dto.getUserId())
				.orElseThrow(() -> new AppBadRequestException("User not found"));

		user.setStatus(GeneralStatus.ACTIVE);
		repository.save(user);

		return AppResponse.builder()
				.success(true)
				.message("OTP is verified. Your registration is complete now.")
				.build();
	}

}
