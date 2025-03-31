package uz.akbar.resto.service;

import java.time.LocalDate;
import java.util.UUID;

import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;

public interface UserService {

	AppResponse getUserById(UUID id);

	AppResponse getCurrentUser(UUID userId);

	AppResponse getUsers(String searchTerm, String firstName, String lastName, String email, GeneralStatus status,
			RoleType role, LocalDate fromDate, LocalDate toDate, int page, int size, String[] sort);
}
