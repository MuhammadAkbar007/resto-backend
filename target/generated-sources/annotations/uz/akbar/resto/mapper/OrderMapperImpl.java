package uz.akbar.resto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.akbar.resto.entity.Order;
import uz.akbar.resto.entity.OrderItem;
import uz.akbar.resto.entity.User;
import uz.akbar.resto.payload.request.CreateOrderItemDto;
import uz.akbar.resto.payload.response.OrderDetailsDto;
import uz.akbar.resto.payload.response.OrderDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-19T14:03:47+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

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

    @Override
    public OrderDetailsDto toDetailsDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();

        orderDetailsDto.setCustomerId( orderCustomerId( order ) );
        orderDetailsDto.setOrderItemDtos( orderItemListToCreateOrderItemDtoList( order.getOrderItems() ) );
        orderDetailsDto.setId( order.getId() );
        orderDetailsDto.setNumber( order.getNumber() );
        orderDetailsDto.setDiscount( order.getDiscount() );
        orderDetailsDto.setTotalPrice( order.getTotalPrice() );
        orderDetailsDto.setOrderStatus( order.getOrderStatus() );
        orderDetailsDto.setCreatedAt( order.getCreatedAt() );
        orderDetailsDto.setVisible( order.getVisible() );

        return orderDetailsDto;
    }

    private UUID orderCustomerId(Order order) {
        User customer = order.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getId();
    }

    protected List<CreateOrderItemDto> orderItemListToCreateOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CreateOrderItemDto> list1 = new ArrayList<CreateOrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemMapper.toDto( orderItem ) );
        }

        return list1;
    }
}
