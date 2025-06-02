package uz.akbar.resto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.payload.request.CreateOrderItemDto;
import uz.akbar.resto.payload.response.OrderItemDetailsDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
		DishMapper.class })
public interface OrderItemMapper {

	@Mapping(target = "dishId", source = "dish.id")
	CreateOrderItemDto toDto(OrderItem orderItem);

	@Mapping(target = "dishDto", source = "dish")
	OrderItemDetailsDto toDetailsDto(OrderItem orderItem);

	List<OrderItemDetailsDto> toDetailsDtoList(List<OrderItem> orderItems);
}
