package uz.akbar.resto.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.mapper.OrderMapper;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderDto;
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

	@Transactional
	@Override
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
}
