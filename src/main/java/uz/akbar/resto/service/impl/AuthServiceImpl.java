package uz.akbar.resto.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import uz.akbar.resto.payload.request.RegisterDto;
import uz.akbar.resto.repository.RefreshTokenRepository;
import uz.akbar.resto.repository.RoleRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.security.jwt.JwtProvider;
import uz.akbar.resto.service.AuthService;

import java.time.Instant;
import java.util.Set;

/** AuthServiceImpl */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public AppResponse registerUser(RegisterDto dto) {

		if (repository.existsByEmailOrPhoneNumber(dto.getEmail(), dto.getPhoneNumber()))
			throw new AppBadRequestException("User already exists");

		Role role = roleRepository
				.findByRoleType(RoleType.CUSTOMER)
				.orElseThrow(() -> new RuntimeException("Role User is not found"));

		User user = User.builder()
				.username(dto.getUsername())
				.email(dto.email())
				.roles(Set.of(role))
				.password(passwordEncoder.encode(dto.password()))
				.status(GeneralStatus.ACTIVE)
				.build();

		User saved = repository.save(user);

		// Set<RoleDto> roleDtos =
		// saved.roles().stream()
		// .map(
		// savedRole ->
		// RoleDto.builder()
		// .id(savedRole.id())
		// .roleType(savedRole.roleType())
		// .build())
		// .collect(Collectors.toSet());
		//
		// UserDto userDto =
		// UserDto.builder()
		// .id(saved.id())
		// .username(saved.username())
		// .email(saved.email())
		// .roles(roleDtos)
		// .build();

		UserDto userDto = userMapper.toDto(saved);

		return AppResponse.builder()
				.success(true)
				.message("User successfully registered")
				.data(userDto)
				.build();
	}

	@Override
	public AppResponse logIn(LogInDto dto) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userDetails.getUser();

		JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);

		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
				.username(user.getUsername())
				.accessToken(accessTokenDto.token())
				.refreshToken(refreshTokenDto.token())
				.accessTokenExpiryTime(accessTokenDto.expiryDate())
				.refreshTokenExpiryTime(refreshTokenDto.expiryDate())
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
		String refreshToken = dto.refreshToken();

		if (!jwtProvider.validateToken(refreshToken)) {
			throw new RefreshTokenException("Invalid refresh token");
		}

		RefreshToken storedToken = refreshTokenRepository
				.findByToken(refreshToken)
				.orElseThrow(
						() -> new RefreshTokenException(
								"Refresh token is not in database!"));

		if (storedToken.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new RefreshTokenException("Refresh token has expired");
		}

		String username = jwtProvider.getUsernameFromToken(refreshToken);

		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());

		JwtDto accessTokenDto = jwtProvider.generateAccessToken(authentication);
		JwtDto refreshTokenDto = jwtProvider.generateRefreshToken(authentication);

		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
				.username(username)
				.accessToken(accessTokenDto.token())
				.refreshToken(refreshTokenDto.token())
				.accessTokenExpiryTime(accessTokenDto.expiryDate())
				.refreshTokenExpiryTime(refreshTokenDto.expiryDate())
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
		String refreshToken = dto.refreshToken();

		if (!jwtProvider.validateToken(refreshToken)) {
			throw new RefreshTokenException("Invalid refresh token");
		}

		RefreshToken storedToken = refreshTokenRepository
				.findByToken(refreshToken)
				.orElseThrow(
						() -> new RefreshTokenException(
								"Refresh token is not in database!"));

		if (storedToken.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(storedToken);
			throw new RefreshTokenException("Refresh token has expired");
		}

		refreshTokenRepository.delete(storedToken);
		SecurityContextHolder.clearContext();
	}
}
