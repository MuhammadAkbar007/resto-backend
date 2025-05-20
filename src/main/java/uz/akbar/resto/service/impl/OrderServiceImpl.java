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

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.entity.Role;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.DeleteType;
import uz.akbar.resto.enums.OrderStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.OrderMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.PaginationData;
import uz.akbar.resto.payload.request.CreateOrderDto;
import uz.akbar.resto.payload.response.OrderDetailsDto;
import uz.akbar.resto.repository.OrderRepository;
import uz.akbar.resto.repository.UserRepository;
import uz.akbar.resto.service.OrderItemService;
import uz.akbar.resto.service.OrderService;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository repository;
	private final OrderMapper mapper;
	private final UserRepository userRepository;
	private final OrderItemService orderItemService;

	@Override
	@Transactional
	public AppResponse create(CreateOrderDto dto) {
		double totalPrice = 0;

		User customer = userRepository.findByIdAndVisibleTrue(dto.getCustomerId())
				.orElseThrow(() -> new AppBadRequestException("Customer not found with id: " + dto.getCustomerId()));

		Order order = Order.builder()
				.number(repository.getNextOrderNumber())
				.discount(dto.getDiscount())
				.totalPrice(totalPrice)
				.orderStatus(dto.getOrderStatus())
				.customer(customer)
				.build();

		Order saved = repository.save(order);

		List<OrderItem> savedOrderItems = orderItemService.saveAll(dto.getOrderItemDtos(), saved);

		double subTotal = savedOrderItems.stream()
				.mapToDouble(item -> item.getPrice() * item.getQuantity())
				.sum();

		saved.setOrderItems(savedOrderItems);
		saved.setTotalPrice(subTotal - dto.getDiscount());

		/*
		 * Since this method is annotated with @Transactional
		 * no need to save again at the end even though saved object is modified
		 * because jpa tracks those changes and flush at the end transaction commit
		 */

		return AppResponse.builder()
				.success(true)
				.message("Order successfully created")
				.data(mapper.toDetailsDto(saved))
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getAll(String searchTerm, Long number, Double discount, Double totalPrice,
			OrderStatus orderStatus, LocalDateTime fromDateTime, LocalDateTime toDateTime, int page, int size,
			String[] sort) {

		Pageable pageable = createPageable(page, size, sort);

		Specification<Order> spec = buildSpecification(searchTerm, number, discount, totalPrice, orderStatus,
				fromDateTime, toDateTime);

		Page<Order> ordersPage = repository.findAll(spec, pageable);
		List<OrderDetailsDto> orderDetailsDtoList = mapper.toDetailsDtoList(ordersPage.getContent());

		return AppResponse.builder()
				.success(true)
				.message("Orders successfully retrieved")
				.data(PaginationData.of(orderDetailsDtoList, ordersPage))
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public AppResponse getById(UUID id) {

		Order order = repository.findByIdAndVisibleTrue(id)
				.orElseThrow(() -> new AppBadRequestException("Order not found with id: " + id));

		return AppResponse.builder()
				.success(true)
				.message("Order successfully retrieved")
				.data(mapper.toDetailsDto(order))
				.build();
	}

	@Override
	@Transactional
	public void delete(UUID id, DeleteType deleteType, User user) {
		switch (deleteType) {
			case HARD:
				Order order = repository.findById(id)
						.orElseThrow(() -> new AppBadRequestException("Order not found with id: " + id));

				if (isNotAdminOrManager(user) && !order.getCustomer().getId().equals(user.getId()))
					throw new AppBadRequestException("You can't delete other's order");

				repository.delete(order);
				break;

			case SOFT:
				Order visibleOrder = repository.findByIdAndVisibleTrue(id)
						.orElseThrow(() -> new AppBadRequestException("Order not found with id: " + id));

				if (isNotAdminOrManager(user) && !visibleOrder.getCustomer().getId().equals(user.getId()))
					throw new AppBadRequestException("You can't delete other's order");

				visibleOrder.setVisible(false);
				/*
				 * Since it's in a transaction jpa context
				 * no need to save this entity
				 * because jpa flushes while commit
				 */
				repository.save(visibleOrder);
				break;

			default:
				throw new AppBadRequestException("Wrong deletion type: " + deleteType);
		}
	}

	private Specification<Order> buildSpecification(String searchTerm, Long number, Double discount, Double totalPrice,
			OrderStatus orderStatus, LocalDateTime fromDateTime, LocalDateTime toDateTime) {

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// General search term
			if (StringUtils.hasText(searchTerm)) {
				String searchPattern = "%" + searchTerm.toLowerCase() + "%";

				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.toString(root.get("number")), searchPattern),
						criteriaBuilder.like(criteriaBuilder.toString(root.get("totalPrice")), searchPattern)));
			}

			// Filter by number
			if (number != null) {
				predicates.add(criteriaBuilder.equal(root.get("number"), number));
			}

			// Filter by discount
			if (discount != null) {
				predicates.add(criteriaBuilder.equal(root.get("discount"), discount));
			}

			// Filter by total price
			if (totalPrice != null) {
				predicates.add(criteriaBuilder.equal(root.get("totalPrice"), totalPrice));
			}

			// Filter by order status
			if (orderStatus != null) {
				predicates.add(criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
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

			// Only show visible orders
			predicates.add(criteriaBuilder.isTrue(root.get("visible")));

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
			orders.add(new Sort.Order(Sort.Direction.DESC, "createdAt"));
		}

		return PageRequest.of(page, size, Sort.by(orders));
	}

	private boolean isValidProperty(String property) {
		List<String> validProperties = Arrays.asList("id", "number", "discount", "totalPrice", "orderStatus",
				"createdAt", "visible");

		return validProperties.contains(property);
	}

	private boolean isNotAdminOrManager(User user) {
		Set<Role> roles = user.getRoles();

		if (roles == null)
			throw new AppBadRequestException("User has no roles");

		return roles.stream()
				.noneMatch(role -> role.getRoleType().equals(RoleType.ROLE_MANAGER)
						|| role.getRoleType().equals(RoleType.ROLE_ADMIN));
	}

}
