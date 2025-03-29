package uz.akbar.resto.payload.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** JwtDto */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtDto {

	String token;

	Instant expiryDate;
}
