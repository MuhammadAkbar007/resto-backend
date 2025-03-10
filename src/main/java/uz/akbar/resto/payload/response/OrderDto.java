package uz.akbar.resto.payload.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.OrderStatus;

/**
 * OrderDto
 */
@Builder
@Getter
@Setter
public class OrderDto {

	private UUID id;

	private Long number;

	@Builder.Default
	private Double discount = 0D;

	private Double totalPrice;

	private OrderStatus orderStatus;
}
