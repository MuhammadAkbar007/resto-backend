package uz.akbar.resto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.Order;
import uz.akbar.resto.payload.response.OrderDetailsDto;
import uz.akbar.resto.payload.response.OrderDto;

/**
 * OrderMapper
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
		OrderItemMapper.class })
public interface OrderMapper {

	OrderDto toDto(Order order);

	@Mapping(target = "customerId", source = "customer.id")
	@Mapping(target = "orderItemDtos", source = "orderItems")
	OrderDetailsDto toDetailsDto(Order order);

	List<OrderDetailsDto> toDetailsDtoList(List<Order> orders);
}
