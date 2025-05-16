package uz.akbar.resto.service;

import java.time.LocalDateTime;

import uz.akbar.resto.enums.DishCategory;
import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;
import uz.akbar.resto.payload.request.UpdateDishDto;

public interface DishService {

	AppResponse create(DishDto dto);

	AppResponse getAll(String searchTerm, String name, String price, String quantityAvailable,
			DishCategory dishCategory, GeneralStatus status, LocalDateTime fromDateTime, LocalDateTime toDateTime,
			int page, int size, String[] sort);

	AppResponse getById(Long id);

	AppResponse edit(Long id, UpdateDishDto dto);

}
