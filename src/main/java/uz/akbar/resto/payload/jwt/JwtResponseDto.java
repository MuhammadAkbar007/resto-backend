package uz.akbar.resto.payload.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** AuthResponseDto */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDto {

	String username;

	String accessToken;

	String refreshToken;

	Instant accessTokenExpiryTime;

	Instant refreshTokenExpiryTime;
}
