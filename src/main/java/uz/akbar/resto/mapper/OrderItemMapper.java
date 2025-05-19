package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.payload.request.CreateOrderItemDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemMapper {

	@Mapping(target = "dishId", source = "dish.id")
	CreateOrderItemDto toDto(OrderItem orderItem);
}
