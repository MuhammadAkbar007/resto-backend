package uz.akbar.resto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import uz.akbar.resto.enums.OrderStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderDto;

public interface OrderService {

	AppResponse create(CreateOrderDto dto);

	AppResponse getAll(String searchTerm, Long number, Double discount, Double totalPrice, OrderStatus orderStatus,
			LocalDateTime fromDateTime, LocalDateTime toDateTime, int page, int size, String[] sort);

	AppResponse getById(UUID id);

}
