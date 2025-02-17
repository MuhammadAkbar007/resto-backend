package uz.akbar.resto.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resto_order_seq")
	@SequenceGenerator(name = "resto_order_seq", sequenceName = "resto_order_seq", allocationSize = 1)
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

	@OneToMany(fetch = FetchType.EAGER)
	private List<OrderItem> orderItems;
}
