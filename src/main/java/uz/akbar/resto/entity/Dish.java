package uz.akbar.resto.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.akbar.resto.entity.template.AbsLongEntity;
import uz.akbar.resto.enums.DishCategory;

/**
 * Dish
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Dish extends AbsLongEntity {

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false)
	private Integer quantityAvailable;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private DishCategory dishCategory;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Attachment photo;
}
