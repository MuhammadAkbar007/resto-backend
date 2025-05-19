package uz.akbar.resto.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.OrderType;

@Getter
@Setter
public class CreateOrderItemDto {

	private UUID id; // used in orderDetailsDto

	@NotNull(message = "Quantity is required")
	@Min(value = 1, message = "Quantity must be at least 1")
	private Integer quantity;

	@NotNull(message = "Price is required")
	@Min(value = 0, message = "Price cannot be negative")
	private Double price;

	private String note;

	@NotNull(message = "Order type is required")
	private OrderType orderType;

	@NotNull(message = "Dish ID is required")
	private Long dishId;
}
