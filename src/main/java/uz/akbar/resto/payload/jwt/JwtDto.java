package uz.akbar.resto.payload.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** JwtDto */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtDto {

	String token;

	Instant expiryDate;
}
