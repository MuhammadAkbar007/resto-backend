package uz.akbar.resto.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uz.akbar.resto.entity.template.AbsUUIDEntity;

/** RefreshToken */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class RefreshToken extends AbsUUIDEntity {

	@Column(unique = true, nullable = false)
	private String token;

	@Column(nullable = false)
	private Instant expiryDate;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	private User user;
}
