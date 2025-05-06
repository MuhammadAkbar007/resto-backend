package uz.akbar.resto.service.impl;

import java.util.UUID;

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
import uz.akbar.resto.repository.RoleRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.AdminService;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final UserMapper mapper;

	@Override
	public AppResponse blockUnblockUser(UUID userId, GeneralStatus status, UUID adminId) {
		if (userId.equals(adminId))
			throw new AppBadRequestException("Wrong id: " + adminId);

		if (status.equals(GeneralStatus.IN_REGISTRATION))
			throw new AppBadRequestException("Wrong status to change");

		User user = repository.findByIdAndVisibleTrue(userId)
				.orElseThrow(() -> new AppBadRequestException("User not found with id: " + userId));

		if (status.equals(user.getStatus()))
			throw new AppBadRequestException("User already in this status");

		user.setStatus(status);
		repository.save(user);

		return AppResponse.builder()
				.success(true)
				.message(
						"User has been "
								+ (status == GeneralStatus.BLOCK ? "blocked" : "activated"))
				.data(userId)
				.build();
	}

	@Override
	public AppResponse assignRole(UUID userId, RoleType roleType, UUID adminId) {
		if (userId.equals(adminId))
			throw new AppBadRequestException("Wrong id: " + adminId);

		User user = repository.findByIdAndVisibleTrue(userId)
				.orElseThrow(() -> new AppBadRequestException("User not found with id: " + userId));

		Role role = roleRepository.findByRoleType(roleType)
				.orElseThrow(() -> new AppBadRequestException("Role not found: " + roleType));

		boolean hasRole = user.getRoles().stream().anyMatch(r -> r.getRoleType().equals(roleType));
		if (hasRole)
			throw new AppBadRequestException("User already has this role: " + roleType);

		user.getRoles().add(role);
		User saved = repository.save(user);

		return AppResponse.builder()
				.success(true)
				.message("Role successfully assigned: " + roleType)
				.data(mapper.toUserDetailsDto(saved))
				.build();
	}

}
