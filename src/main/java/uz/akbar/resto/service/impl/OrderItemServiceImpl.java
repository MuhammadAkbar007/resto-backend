package uz.akbar.resto.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uz.akbar.resto.entity.Dish;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.exception.AppBadRequestException;
import uz.akbar.resto.payload.request.CreateOrderItemDto;
import uz.akbar.resto.repository.DishRepository;
import uz.akbar.resto.repository.OrderItemRepository;
import uz.akbar.resto.service.OrderItemService;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemRepository repository;
	private final DishRepository dishRepository;

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

}
