package uz.akbar.resto.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.payload.response.OrderDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-04T19:25:14+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto.OrderDtoBuilder orderDto = OrderDto.builder();

        orderDto.id( order.getId() );
        orderDto.number( order.getNumber() );
        orderDto.discount( order.getDiscount() );
        orderDto.totalPrice( order.getTotalPrice() );
        orderDto.orderStatus( order.getOrderStatus() );

        return orderDto.build();
    }
}
