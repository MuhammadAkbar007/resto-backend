package uz.akbar.resto.service;

import uz.akbar.resto.payload.AppResponse;
import uz.akbar.resto.payload.request.LogInDto;
import uz.akbar.resto.payload.request.OtpVerificationDto;
import uz.akbar.resto.payload.request.RefreshTokenRequestDto;
import uz.akbar.resto.payload.request.RegisterDto;

/** AuthService */
public interface AuthService {

	AppResponse registerUser(RegisterDto dto);

	AppResponse logIn(LogInDto dto);

	AppResponse refreshToken(RefreshTokenRequestDto dto);

	void logout(RefreshTokenRequestDto dto);

	AppResponse verifyOtp(OtpVerificationDto dto);
}
