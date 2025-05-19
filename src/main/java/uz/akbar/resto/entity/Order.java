package uz.akbar.resto.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.akbar.resto.entity.template.AbsUUIDEntity;
import uz.akbar.resto.enums.OrderStatus;

/** Order */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "orders")
public class Order extends AbsUUIDEntity {

	@Column(nullable = false, unique = true)
	private Long number;

	@Builder.Default
	@Column(nullable = false)
	private Double discount = 0D;

	@Column(nullable = false)
	private Double totalPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User customer;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<OrderItem> orderItems;
}
