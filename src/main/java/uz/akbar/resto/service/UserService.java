package uz.akbar.resto.service;

import java.util.UUID;

import uz.akbar.resto.payload.AppResponse;

public interface UserService {

	AppResponse getUserById(UUID id);
}
