package uz.akbar.resto.service;

import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.DishDto;

public interface DishService {

	AppResponse create(DishDto dto);

}
