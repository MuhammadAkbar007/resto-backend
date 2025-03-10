package uz.akbar.resto.payload.jwt;

import lombok.Builder;

import java.time.Instant;

/** AuthResponseDto */
@Builder
public class JwtResponseDto {

	String username;

	String accessToken;

	String refreshToken;

	Instant accessTokenExpiryTime;

	Instant refreshTokenExpiryTime;
}
