package uz.akbar.resto.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.payload.response.OrderDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-31T06:24:46+0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.41.0.z20250115-2156, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto.OrderDtoBuilder orderDto = OrderDto.builder();

        orderDto.discount( order.getDiscount() );
        orderDto.id( order.getId() );
        orderDto.number( order.getNumber() );
        orderDto.orderStatus( order.getOrderStatus() );
        orderDto.totalPrice( order.getTotalPrice() );

        return orderDto.build();
    }
}
