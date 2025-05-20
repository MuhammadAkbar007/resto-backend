package uz.akbar.resto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import uz.akbar.resto.entity.User;
import uz.akbar.resto.enums.DeleteType;
import uz.akbar.resto.enums.OrderStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderDto;
import uz.akbar.resto.payload.request.UpdateOrderDto;

public interface OrderService {

	AppResponse create(CreateOrderDto dto);

	AppResponse getAll(String searchTerm, Long number, Double discount, Double totalPrice, OrderStatus orderStatus,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, int page, int size, String[] sort);

	AppResponse getById(UUID id);

	void delete(UUID id, DeleteType deleteType, User user);

	AppResponse update(UUID id, UpdateOrderDto dto, User user);
}
