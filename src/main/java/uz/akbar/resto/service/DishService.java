package uz.akbar.resto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import uz.akbar.resto.enums.DishCategory;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;

public interface DishService {

	AppResponse create(DishDto dto);

	AppResponse getAll(String searchTerm, String name, String price, String quantityAvailable,
			DishCategory dishCategory, GeneralStatus status, LocalDateTime fromDateTime, LocalDateTime toDateTime,
			int page, int size, String[] sort);

	AppResponse getById(Long id);

}
