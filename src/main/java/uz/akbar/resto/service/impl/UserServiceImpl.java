package uz.akbar.resto.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.UserMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.response.UserDetailsDto;
import uz.akbar.resto.payload.response.UserDto;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository repository;
	private final UserMapper mapper;

	@Override
	@Transactional(readOnly = true)
	public AppResponse getUserById(UUID id) {

		User user = repository.findById(id)
				.orElseThrow(() -> new AppBadRequestException("User not found"));

		UserDetailsDto userDetailsDto = mapper.toUserDetailsDto(user);

		return AppResponse.builder()
				.success(true)
				.message("User info")
				.data(user)
				.data(userDetailsDto)
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getCurrentUser(UUID userId) {

		User user = repository.findById(userId)
				.orElseThrow(() -> new AppBadRequestException("user not found"));

		UserDto userDto = mapper.toUserDto(user);

		return AppResponse.builder()
				.success(true)
				.message("User details")
				.data(userDto)
				.build();
	}

	@Override
	public AppResponse getUsers(String searchTerm, String firstName, String lastName, String email,
			GeneralStatus status, RoleType role, LocalDate fromDate, LocalDate toDate, int page, int size,
			String[] sort) {

		Pageable pageable = createPageable(page, size, sort);

		return null;
	}

	/**
	 * Creates a Pageable object for pagination and sorting.
	 *
	 * @param page Zero-based page index
	 * @param size Page size
	 * @param sort String array with format ["property,direction",
	 *             "property,direction"]
	 *             For example: ["createdAt,desc", "firstName,asc"]
	 * 
	 * @return A configured Pageable object
	 */
	private Pageable createPageable(int page, int size, String[] sort) {
		if (page < 0)
			page = 0;

		if (size < 1)
			size = 10;

		List<Sort.Order> orders = new ArrayList<>();

		if (sort != null && sort.length > 0) {
			for (String sortItem : sort) {
				if (sortItem.contains(",")) {
					String[] parts = sortItem.split(",");
					String property = parts[0];
					Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC
							: Sort.Direction.DESC;

					orders.add(new Sort.Order(direction, property));
				} else {
					orders.add(new Sort.Order(Sort.Direction.ASC, sortItem));
				}
			}
		}

		if (orders.isEmpty()) {
			orders.add(new Sort.Order(Sort.Direction.ASC, "createdAt"));
		}

		return PageRequest.of(page, size, Sort.by(orders));
	}

}
