package uz.akbar.resto.service;

import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.CreateOrderDto;

public interface OrderService {

	AppResponse create(CreateOrderDto dto);

}
