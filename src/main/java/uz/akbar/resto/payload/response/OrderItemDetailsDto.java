package uz.akbar.resto.payload.response;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.OrderType;
import uz.akbar.resto.payload.request.DishDto;

@Getter
@Setter
public class OrderItemDetailsDto {

	private UUID id;

	@Min(value = 1, message = "Quantity must be at least 1")
	private Integer quantity;

	@Min(value = 0, message = "Price cannot be negative")
	private Double price;

	private String note;

	private OrderType orderType;

	private Instant createdAt;

	private Boolean visible;

	private DishDto dishDto;
}
