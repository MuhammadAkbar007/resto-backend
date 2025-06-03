package uz.akbar.resto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.OrderType;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderItemDto;
import uz.akbar.resto.payload.request.UpdateOrderItemDto;

public interface OrderItemService {

	List<OrderItem> saveAll(List<CreateOrderItemDto> orderItemDtos, Order savingOrder);

	AppResponse getAll(String searchTerm, Integer quantity, Double price, OrderType orderType,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, String dishName, Long orderNumber, int page, int size,
			String[] sort);

	AppResponse getById(UUID id);

	AppResponse update(UUID id, UpdateOrderItemDto dto, User user);
}
