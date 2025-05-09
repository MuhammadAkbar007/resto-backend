package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.Dish;
import uz.akbar.resto.payload.request.DishDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DishMapper {

	@Mapping(target = "orderItems", ignore = true)
	@Mapping(target = "photo", ignore = true)
	Dish toEntity(DishDto dto);

	@Mapping(target = "photoId", source = "photo.id")
	DishDto toDto(Dish dish);
}
