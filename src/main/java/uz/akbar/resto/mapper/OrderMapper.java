package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import uz.akbar.resto.entity.Order;
import uz.akbar.resto.payload.response.OrderDto;

/**
 * OrderMapper
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

	OrderDto toDto(Order order);
}
