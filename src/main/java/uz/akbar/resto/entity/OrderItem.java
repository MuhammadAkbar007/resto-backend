package uz.akbar.resto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import uz.akbar.resto.entity.template.AbsUUIDEntity;
import uz.akbar.resto.enums.OrderType;

/** OrderItem */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class OrderItem extends AbsUUIDEntity {

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Double price;

	private String note;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderType orderType;

	@ManyToOne
	private Order order;
}
