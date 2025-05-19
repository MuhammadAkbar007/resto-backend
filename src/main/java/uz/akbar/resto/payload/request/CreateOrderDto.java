package uz.akbar.resto.payload.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.OrderStatus;

@Getter
@Setter
public class CreateOrderDto {

	@DecimalMin(value = "0.0", message = "Discount cannot be negative")
	private Double discount = 0D;

	@NotNull(message = "Order status is required")
	private OrderStatus orderStatus;

	@NotNull(message = "Customer ID is required")
	private UUID customerId;

	@Valid
	@NotEmpty(message = "Order should have at least one order item")
	private List<CreateOrderItemDto> orderItemDtos;
}
