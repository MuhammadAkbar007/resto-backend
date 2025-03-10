package uz.akbar.resto.mapper;

import org.mapstruct.Mapper;

import uz.akbar.resto.entity.Order;
import uz.akbar.resto.payload.response.OrderDto;

/**
 * OrderMapper
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

	OrderDto toDto(Order order);
}
