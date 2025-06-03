package uz.akbar.resto.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Dish;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.OrderType;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.OrderItemMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.PaginationData;
import uz.akbar.resto.payload.request.CreateOrderItemDto;
import uz.akbar.resto.payload.request.UpdateOrderItemDto;
import uz.akbar.resto.payload.response.OrderItemDetailsDto;
import uz.akbar.resto.repository.DishRepository;
import uz.akbar.resto.repository.OrderItemRepository;
import uz.akbar.resto.service.OrderItemService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemRepository repository;
	private final DishRepository dishRepository;
	private final OrderItemMapper mapper;

	/*
	 * Since this method is called from OrderServiceImpl's method create
	 * and that method is annotated with @Transactional,
	 * i am not adding the annotation here
	 */
	@Override
	public List<OrderItem> saveAll(List<CreateOrderItemDto> orderItemDtos, Order savingOrder) {
		List<OrderItem> readyToSave = new ArrayList<>();

		for (CreateOrderItemDto dto : orderItemDtos) {
			Dish dish = dishRepository.findByIdAndVisibleTrue(dto.getDishId())
					.orElseThrow(() -> new AppBadRequestException("Dish not found with id: " + dto.getDishId()));

			OrderItem orderItem = OrderItem.builder()
					.quantity(dto.getQuantity())
					.price(dto.getPrice())
					.note(dto.getNote())
					.orderType(dto.getOrderType())
					.order(savingOrder)
					.dish(dish)
					.build();

			readyToSave.add(orderItem);
		}

		return repository.saveAll(readyToSave);
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getAll(String searchTerm, Integer quantity, Double price, OrderType orderType,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, String dishName, Long orderNumber, int page, int size,
			String[] sort) {

		Pageable pageable = createPageable(page, size, sort);

		Specification<OrderItem> spec = buildSpecification(searchTerm, quantity, price, orderType, fromDateTime,
				toDateTime, dishName, orderNumber);

		Page<OrderItem> orderItemsPage = repository.findAll(spec, pageable);
		List<OrderItemDetailsDto> orderItemsDetailsDtoList = mapper.toDetailsDtoList(orderItemsPage.getContent());

		return AppResponse.builder()
				.success(true)
				.message("Order items retrieved successfully")
				.data(PaginationData.of(orderItemsDetailsDtoList, orderItemsPage))
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getById(UUID id) {
		OrderItem orderItem = repository.findByIdAndVisibleTrue(id)
				.orElseThrow(() -> new AppBadRequestException("Order item not found with id: " + id));

		return AppResponse.builder()
				.success(true)
				.message("Order item successfully retrieved")
				.data(mapper.toDetailsDto(orderItem))
				.build();
	}

	@Override
	@Transactional
	public AppResponse update(UUID id, UpdateOrderItemDto dto, User user) {
		OrderItem orderItem = repository.findByIdAndVisibleTrue(id)
				.orElseThrow(() -> new AppBadRequestException("Order item is not found with id: " + id));

		UUID customerId = orderItem.getOrder().getCustomer().getId();

		if (!hasAdminOrManagerRole(user) && !user.getId().equals(customerId))
			throw new AppBadRequestException("You don't have permission to change other's order items");

		if (!dto.getDishId().equals(null)) {
			Long dishId = dto.getDishId();
			Dish dish = dishRepository.findByIdAndVisibleTrue(dishId)
					.orElseThrow(() -> new AppBadRequestException("Dish is not found with id: " + dishId));

			orderItem.setDish(dish);
		}

		if (!dto.getQuantity().equals(null) && dto.getQuantity() > 0)
			orderItem.setQuantity(dto.getQuantity());

		if (!dto.getPrice().equals(null) && dto.getPrice() > 0)
			orderItem.setPrice(dto.getPrice());

		if (!dto.getNote().equals(null))
			orderItem.setNote(dto.getNote());

		OrderItem saved = repository.save(orderItem);

		return AppResponse.builder()
				.success(true)
				.message("Order item successfully updated")
				.data(mapper.toDetailsDto(saved))
				.build();
	}

	@Override
	@Transactional
	public AppResponse updateOrderType(UUID id, OrderType orderType, User user) {
		OrderItem orderItem = repository.findByIdAndVisibleTrue(id)
				.orElseThrow(() -> new AppBadRequestException("Order item is not found with id: " + id));

		UUID customerId = orderItem.getOrder().getCustomer().getId();

		if (!hasAdminOrManagerOrEmployeeRole(user) && !user.getId().equals(customerId))
			throw new AppBadRequestException("You don't have permission to change other's order items");

		orderItem.setOrderType(orderType);
		OrderItem saved = repository.save(orderItem);

		return AppResponse.builder()
				.success(true)
				.message("Order type successfully updated to " + orderType)
				.data(mapper.toDetailsDto(saved))
				.build();
	}

	private Specification<OrderItem> buildSpecification(String searchTerm, Integer quantity, Double price,
			OrderType orderType, LocalDateTime fromDateTime, LocalDateTime toDateTime, String dishName,
			Long orderNumber) {

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			Join<OrderItem, Order> orderJoin = root.join("order", JoinType.LEFT);
			Join<OrderItem, Dish> dishJoin = root.join("dish", JoinType.LEFT);

			if (StringUtils.hasText(searchTerm)) {
				String searchPattern = "%" + searchTerm.toLowerCase() + "%";

				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.toString(root.get("quantity")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.toString(root.get("price")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.toString(orderJoin.get("number")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.lower(dishJoin.get("name")), searchPattern)));
			}

			if (quantity != null)
				predicates.add(criteriaBuilder.equal(root.get("price"), price));

			if (orderType != null)
				predicates.add(criteriaBuilder.equal(root.get("orderType"), orderType));

			if (StringUtils.hasText(dishName)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(dishJoin.get("name")),
						"%" + dishName.toLowerCase() + "%"));
			}

			if (orderNumber != null)
				predicates.add(criteriaBuilder.equal(orderJoin.get("number"), orderNumber));

			if (fromDateTime != null) {
				Instant fromInstant = fromDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
			}

			if (toDateTime != null) {
				Instant toInstant = toDateTime.atZone(ZoneId.systemDefault()).toInstant();
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toInstant));
			}

			predicates.add(criteriaBuilder.isTrue(root.get("visible")));
			predicates.add(criteriaBuilder.isTrue(orderJoin.get("visible")));
			predicates.add(criteriaBuilder.isTrue(dishJoin.get("visible")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

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

					if (!isValidProperty(property))
						continue;

					Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC
							: Sort.Direction.DESC;

					orders.add(new Sort.Order(direction, property));
				} else {
					if (isValidProperty(sortItem))
						orders.add(new Sort.Order(Sort.Direction.ASC, sortItem));
				}
			}
		}

		if (orders.isEmpty())
			orders.add(new Sort.Order(Sort.Direction.DESC, "createdAt"));

		return PageRequest.of(page, size, Sort.by(orders));
	}

	private boolean isValidProperty(String property) {
		List<String> validProperties = Arrays.asList("id", "quantity", "price", "note", "orderType", "createdAt",
				"visible", "order.number", "dish.name");

		return validProperties.contains(property);
	}

	private boolean hasAdminOrManagerRole(User user) {
		Set<Role> roles = user.getRoles();

		if (roles == null)
			return false;

		return roles.stream()
				.anyMatch(role -> role.getRoleType() == RoleType.ROLE_ADMIN
						|| role.getRoleType() == RoleType.ROLE_MANAGER);
	}

	private boolean hasAdminOrManagerOrEmployeeRole(User user) {
		Set<Role> roles = user.getRoles();

		if (roles == null)
			return false;

		return roles.stream()
				.anyMatch(role -> role.getRoleType() == RoleType.ROLE_ADMIN
						|| role.getRoleType() == RoleType.ROLE_MANAGER
						|| role.getRoleType() == RoleType.ROLE_EMPLOYEE);
	}

}
