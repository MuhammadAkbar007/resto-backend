package uz.akbar.resto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;

public interface UserService {

	AppResponse getUserById(UUID id);

	AppResponse getCurrentUser(UUID userId);

	AppResponse getAllUsers(String searchTerm, String firstName, String lastName, String email, String phoneNumber,
			GeneralStatus status, RoleType role, LocalDateTime fromDateTime, LocalDateTime toDateTime, int page,
			int size, String[] sort);

	void delete(UUID id, User user);
}
