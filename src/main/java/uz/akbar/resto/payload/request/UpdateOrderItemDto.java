package uz.akbar.resto.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderItemDto {

	private Integer quantity;

	private Double price;

	private String note;

	private Long dishId;
}
