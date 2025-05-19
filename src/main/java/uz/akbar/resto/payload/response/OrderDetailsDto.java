package uz.akbar.resto.payload.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.OrderStatus;
import uz.akbar.resto.payload.request.CreateOrderItemDto;

@Getter
@Setter
public class OrderDetailsDto {

	private UUID id;

	private Long number;

	private Double discount = 0D;

	private Double totalPrice;

	private OrderStatus orderStatus;

	private Instant createdAt;

	private Boolean visible;

	private UUID customerId;

	private List<CreateOrderItemDto> orderItemDtos;
}
