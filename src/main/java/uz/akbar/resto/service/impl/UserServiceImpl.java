package uz.akbar.resto.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.UserMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.response.UserDetailsDto;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private UserRepository repository;
	private UserMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public AppResponse getUserById(UUID id) {
		User user = repository.findById(id)
				.orElseThrow(() -> new AppBadRequestException("User not found"));

		UserDetailsDto userDetailsDto = mapper.toUserDetailsDto(user);

		return AppResponse.builder()
				.success(true)
				.message("User info")
				.data(userDetailsDto)
				.build();
	}

}
