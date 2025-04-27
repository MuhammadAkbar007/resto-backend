package uz.akbar.resto.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.StorageType;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.exception.FileUploadException;
import uz.akbar.resto.mapper.UserMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.PaginationData;
import uz.akbar.resto.payload.request.UpdateUserRequestDto;
import uz.akbar.resto.payload.response.UserDetailsDto;
import uz.akbar.resto.payload.response.UserDto;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.AttachmentService;
import uz.akbar.resto.service.UserService;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository repository;
	private final UserMapper mapper;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AttachmentService attachmentService;

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
	@Transactional(readOnly = true)
	public AppResponse getAllUsers(String searchTerm, String firstName, String lastName, String email,
			String phoneNumber, GeneralStatus status, RoleType role, LocalDateTime fromDateTime,
			LocalDateTime toDateTime, int page, int size, String[] sort) {

		Pageable pageable = createPageable(page, size, sort);
		Specification<User> spec = buildSpecification(searchTerm, email, phoneNumber, fromDateTime, toDateTime, status,
				role);

		Page<User> usersPage = repository.findAll(spec, pageable);

		List<UserDetailsDto> usersDtos = usersPage.getContent().stream()
				.map(mapper::toUserDetailsDto)
				.collect(Collectors.toList());

		PaginationData<UserDetailsDto> paginationData = PaginationData.of(usersDtos, usersPage);

		return AppResponse.builder()
				.success(true)
				.message("Users retrieved successfully")
				.data(paginationData)
				.build();
	}

	@Override
	@Transactional
	public void delete(UUID id, User user) {
		boolean isAdmin = hasAdminRole(user);

		if (!isAdmin && !id.equals(user.getId()))
			throw new AppBadRequestException("Wrong id " + id);

		User deletingUser = repository.findById(id)
				.orElseThrow(() -> new AppBadRequestException("user not found with id " + id));

		deletingUser.setVisible(false);
		repository.save(deletingUser);
	}

	@Override
	@Transactional
	public AppResponse update(UUID id, UpdateUserRequestDto dto, User user) {
		String email = dto.getEmail();
		String phoneNumber = dto.getPhoneNumber();

		User editingUser = repository.findByIdAndVisibleTrue(id)
				.orElseThrow(() -> new AppBadRequestException("User not found with id: " + id));

		boolean isAdmin = hasAdminRole(user);

		if (!isAdmin && !id.equals(user.getId()))
			throw new AppBadRequestException("Wrong id: " + id);

		if (StringUtils.hasText(email)) {
			if (repository.existsByEmail(email))
				throw new AppBadRequestException("user already exists with email: " + email);

			editingUser.setEmail(email);
		}

		if (StringUtils.hasText(phoneNumber)) {
			if (repository.existsByPhoneNumber(phoneNumber))
				throw new AppBadRequestException("user already exists with phoneNumber: " + phoneNumber);

			editingUser.setPhoneNumber(phoneNumber);
		}

		if (StringUtils.hasText(dto.getFirstName()))
			editingUser.setFirstName(dto.getFirstName());

		if (StringUtils.hasText(dto.getLastName()))
			editingUser.setLastName(dto.getLastName());

		if (StringUtils.hasText(dto.getPassword()))
			editingUser.setPassword(passwordEncoder.encode(dto.getPassword()));

		User saved = repository.save(editingUser);

		return AppResponse.builder()
				.success(true)
				.message("user successfully updated")
				.data(isAdmin ? mapper.toUserDetailsDto(saved) : mapper.toUserDto(saved))
				.build();

	}

	/**
	 * Update a user's profile photo
	 * 
	 * @param userId      ID of the user to update (if null, updates the current
	 *                    user)
	 * @param photo       the new photo file
	 * @param currentUser the currently authenticated user
	 * @return AppResponse with the updated user information
	 */
	@Override
	@Transactional
	public AppResponse updateUserPhoto(UUID userId, MultipartFile photo, User currentUser) {
		User userToUpdate;
		boolean isAdmin = hasAdminRole(currentUser);

		if (userId == null) {
			userToUpdate = currentUser;
		} else {
			if (!isAdmin && !userId.equals(currentUser.getId()))
				throw new AppBadRequestException("You don't have permission to update another person's photo");

			userToUpdate = repository.findByIdAndVisibleTrue(userId)
					.orElseThrow(() -> new AppBadRequestException("User not found with id: " + userId));
		}

		if (photo.isEmpty())
			throw new FileUploadException(HttpStatus.BAD_REQUEST, "Failed to store an empty file");

		String contentType = photo.getContentType();
		if (contentType == null || !contentType.startsWith("image/"))
			throw new FileUploadException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only image files are allowed");

		if (userToUpdate.getPhoto() != null)
			attachmentService.deleteAttachment(userToUpdate.getPhoto().getId());

		Attachment savedAttachment = attachmentService.saveAttachment(photo, StorageType.FILE_SYSTEM);
		userToUpdate.setPhoto(savedAttachment);

		User savedUser = repository.save(userToUpdate);

		return AppResponse.builder()
				.success(true)
				.message("Profile photo updated successfully")
				.data(isAdmin ? mapper.toUserDetailsDto(savedUser) : mapper.toUserDto(savedUser))
				.build();
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

					if (!isValidProperty(property)) {
						continue;
					}

					Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC
							: Sort.Direction.DESC;

					orders.add(new Sort.Order(direction, property));
				} else {
					if (isValidProperty(sortItem)) {
						orders.add(new Sort.Order(Sort.Direction.ASC, sortItem));
					}
				}
			}
		}

		if (orders.isEmpty()) {
			orders.add(new Sort.Order(Sort.Direction.ASC, "createdAt"));
		}

		return PageRequest.of(page, size, Sort.by(orders));
	}

	private Specification<User> buildSpecification(String searchTerm, String email, String phoneNumber,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, GeneralStatus status, RoleType role) {

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.hasText(searchTerm)) {
				String searchPattern = "%" + searchTerm.toLowerCase() + "%";

				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), searchPattern)));
			}

			if (StringUtils.hasText(email))
				predicates.add(criteriaBuilder.equal(root.get("email"), email));

			if (StringUtils.hasText(phoneNumber))
				predicates.add(criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber));

			// Apply fromDate filter (e.g., createdAt should be >= fromDate)
			if (fromDateTime != null) {
				Instant fromInstant = fromDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
			}

			// Apply toDate filter (e.g., createdAt should be <= toDate)
			if (toDateTime != null) {
				Instant toInstant = toDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toInstant));
			}

			if (status != null)
				predicates.add(criteriaBuilder.equal(root.get("status"), status));

			if (role != null) {
				Join<User, Role> roleJoin = root.join("roles", JoinType.INNER);
				predicates.add(criteriaBuilder.equal(roleJoin.get("roleType"), role));
				query.distinct(true);
			}

			predicates.add(criteriaBuilder.isTrue(root.get("visible")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}

	private boolean isValidProperty(String property) {
		List<String> validProperties = Arrays.asList("id", "firstName", "lastName", "email", "phoneNumber", "status",
				"createdAt", "visible");

		return validProperties.contains(property);
	}

	private boolean hasAdminRole(User user) {
		Set<Role> roles = user.getRoles();

		if (roles == null)
			return false;

		return roles.stream()
				.anyMatch(role -> role.getRoleType() == RoleType.ROLE_ADMIN);
	}

}
