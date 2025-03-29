package uz.akbar.resto.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uz.akbar.resto.entity.template.AbsUUIDEntity;

/**
 * OtpVerification
 */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class OtpVerification extends AbsUUIDEntity {

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String otp;

	@Column(nullable = false)
	private Instant expiryTime;

	private boolean isVerified;

	@Builder.Default
	@Column(nullable = false)
	private int attemptCount = 0;

	public void incrementAttempt() {
		attemptCount++;
	}
}
