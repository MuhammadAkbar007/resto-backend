package uz.akbar.resto.service;

import java.util.List;

import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.payload.request.CreateOrderItemDto;

public interface OrderItemService {

	List<OrderItem> saveAll(List<CreateOrderItemDto> orderItemDtos, Order savingOrder);

}
