package uz.akbar.resto.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Attachment;
import uz.akbar.resto.entity.Dish;
import uz.akbar.resto.enums.DishCategory;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.DishMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.PaginationData;
import uz.akbar.resto.payload.request.DishDto;
import uz.akbar.resto.repository.DishRepository;
import uz.akbar.resto.service.AttachmentService;
import uz.akbar.resto.service.DishService;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

	private final DishRepository repository;
	private final DishMapper mapper;
	private final AttachmentService attachmentService;

	@Override
	@Transactional
	public AppResponse create(DishDto dto) {

		if (repository.existsByName(dto.getName()))
			throw new AppBadRequestException("Dish already exists with the name: " + dto.getName());

		Attachment photo = attachmentService.findAttachmentById(dto.getPhotoId());

		Dish dish = mapper.toEntity(dto);
		dish.setPhoto(photo);

		Dish saved = repository.save(dish);

		return AppResponse.builder()
				.success(true)
				.message("Dish created successfully")
				.data(mapper.toDto(saved))
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getAll(String searchTerm, String name, String price, String quantityAvailable,
			DishCategory dishCategory, GeneralStatus status, LocalDateTime fromDateTime, LocalDateTime toDateTime,
			int page, int size, String[] sort) {

		Pageable pageable = createPageable(page, size, sort);

		Specification<Dish> spec = buildSpecification(searchTerm, name, price, quantityAvailable, dishCategory, status,
				fromDateTime, toDateTime);

		Page<Dish> dishesPage = repository.findAll(spec, pageable);

		List<DishDto> dishDtos = dishesPage.getContent().stream()
				.map(mapper::toDto)
				.collect(Collectors.toList());

		return AppResponse.builder()
				.success(true)
				.message("Dishes retrieved successfully")
				.data(PaginationData.of(dishDtos, dishesPage))
				.build();
	}

	private Specification<Dish> buildSpecification(String searchTerm, String name, String price,
			String quantityAvailable, DishCategory dishCategory, GeneralStatus status, LocalDateTime fromDateTime,
			LocalDateTime toDateTime) {

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// General search term
			if (StringUtils.hasText(searchTerm)) {
				String searchPattern = "%" + searchTerm.toLowerCase() + "%";

				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern)));
			}

			// Filter by name
			if (StringUtils.hasText(name)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("name")),
						"%" + name.toLowerCase() + "%"));
			}

			// Filter by price
			if (StringUtils.hasText(price)) {
				try {
					Double priceValue = Double.parseDouble(price);
					predicates.add(criteriaBuilder.equal(root.get("price"), priceValue));
				} catch (NumberFormatException e) {
					// NOTE: Ignore invalid price format
				}
			}

			// Filter by quantity available
			if (StringUtils.hasText(quantityAvailable)) {
				try {
					Integer quantityValue = Integer.parseInt(quantityAvailable);
					predicates.add(criteriaBuilder.equal(root.get("quantityAvailable"), quantityValue));
				} catch (NumberFormatException e) {
					// NOTE: Ignore invalid quantity format
				}
			}

			// Filter by category
			if (dishCategory != null) {
				predicates.add(criteriaBuilder.equal(root.get("dishCategory"), dishCategory));
			}

			// Filter by status
			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), status));
			}

			// Filter by creation date range
			if (fromDateTime != null) {
				Instant fromInstant = fromDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
			}

			if (toDateTime != null) {
				Instant toInstant = toDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toInstant));
			}

			// Only show visible dishes
			predicates.add(criteriaBuilder.isTrue(root.get("visible")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
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

	private boolean isValidProperty(String property) {
		List<String> validProperties = Arrays.asList("id", "name", "price", "quantityAvailable", "dishCategory",
				"createdAt", "visible");

		return validProperties.contains(property);
	}

}
