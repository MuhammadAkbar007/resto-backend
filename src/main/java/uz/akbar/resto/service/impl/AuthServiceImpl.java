package uz.akbar.resto.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.RefreshToken;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.exception.RefreshTokenException;
import uz.akbar.resto.mapper.UserMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.jwt.JwtDto;
import uz.akbar.resto.payload.jwt.JwtResponseDto;
import uz.akbar.resto.payload.request.LogInDto;
import uz.akbar.resto.payload.request.OtpVerificationDto;
import uz.akbar.resto.payload.request.RefreshTokenRequestDto;
import uz.akbar.resto.payload.request.RegisterDto;
import uz.akbar.resto.payload.response.UserDto;
import uz.akbar.resto.repository.RefreshTokenRepository;
import uz.akbar.resto.repository.RoleRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.security.CustomUserDetails;
import uz.akbar.resto.security.jwt.JwtProvider;
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
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;

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

		Role roleCustomer = roleRepository.findByRoleType(RoleType.CUSTOMER)
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
		emailService.sendOtpEmail(saved.getEmail(), otp, otpVerificationService.getExpiryMinutes());

		UserDto userDto = userMapper.toUserDto(saved);

		return AppResponse.builder()
				.success(true)
				.message("Validation token has been sent to your email for register verification")
				.data(userDto)
				.build();
	}

	@Override
	public AppResponse logIn(LogInDto dto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userDetails.getUser();

		if (!user.getStatus().equals(GeneralStatus.ACTIVE))
			throw new AppBadRequestException("Wrong status");

		SecurityContextHolder.getContext().setAuthentication(authentication);

		JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);

		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
				.username(user.getEmail())
				.accessToken(accessTokenDto.getToken())
				.refreshToken(refreshTokenDto.getToken())
				.accessTokenExpiryTime(accessTokenDto.getExpiryDate())
				.refreshTokenExpiryTime(refreshTokenDto.getExpiryDate())
				.build();

		return AppResponse.builder()
				.success(true)
				.message("User successfully logged in")
				.data(jwtResponseDto)
				.build();
	}

	@Override
	@Transactional
	public AppResponse refreshToken(RefreshTokenRequestDto dto) {
		String refreshToken = dto.getRefreshToken();

		if (!jwtProvider.validateToken(refreshToken))
			throw new RefreshTokenException("Invalid refresh token");

		RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new RefreshTokenException("Refresh token is not found"));

		if (storedToken.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new RefreshTokenException("Refresh token has expired");
		}

		String username = jwtProvider.getUsernameFromToken(refreshToken);

		User user = repository.findByEmail(username).orElseThrow(() -> new AppBadRequestException("User not found"));

		CustomUserDetails userDetails = new CustomUserDetails(user);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);

		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
				.username(username)
				.accessToken(accessTokenDto.getToken())
				.refreshToken(refreshTokenDto.getToken())
				.accessTokenExpiryTime(accessTokenDto.getExpiryDate())
				.refreshTokenExpiryTime(refreshTokenDto.getExpiryDate())
				.build();

		return AppResponse.builder()
				.success(true)
				.message("Tokens successfully regenerated")
				.data(jwtResponseDto)
				.build();
	}

	@Override
	@Transactional
	public void logout(RefreshTokenRequestDto dto) {
		String refreshToken = dto.getRefreshToken();

		if (!jwtProvider.validateToken(refreshToken))
			throw new RefreshTokenException("Invalid refresh token");

		RefreshToken storedToken = refreshTokenRepository
				.findByToken(refreshToken)
				.orElseThrow(() -> new RefreshTokenException("Refresh token is not in database!"));

		if (storedToken.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new RefreshTokenException("Refresh token has expired");
		}

		refreshTokenRepository.delete(storedToken);
		SecurityContextHolder.clearContext();
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
