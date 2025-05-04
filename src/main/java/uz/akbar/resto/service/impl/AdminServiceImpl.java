package uz.akbar.resto.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.AdminService;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository repository;

	@Override
	@Transactional
	public AppResponse blockUnblockUser(UUID userId, GeneralStatus status, UUID adminId) {
		if (userId.equals(adminId))
			throw new AppBadRequestException("Wrong id: " + adminId);

		if (status.equals(GeneralStatus.IN_REGISTRATION))
			throw new AppBadRequestException("Wrong status to change");

		User user = repository.findByIdAndVisibleTrue(userId)
				.orElseThrow(() -> new AppBadRequestException("User not found with id " + userId));

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

}
