package uz.akbar.resto.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.akbar.resto.enums.DishCategory;

@Getter
@Setter
public class DishDto {

	private Long id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Price is required")
	@Min(value = 0, message = "Price must be greater than or equal to 0")
	private Double price;

	@NotNull(message = "Quantity available is required")
	@Min(value = 0, message = "Quantity must be greater than or equal to 0")
	private Integer quantityAvailable;

	@NotNull(message = "Dish category is required")
	private DishCategory dishCategory;

	@NotNull(message = "Photo id is required")
	private UUID photoId;
}
