package uz.akbar.resto.service;

import java.util.UUID;

import uz.akbar.resto.enums.GeneralStatus;
import uz.akbar.resto.enums.RoleType;
import uz.akbar.resto.payload.AppResponse;

public interface AdminService {

	AppResponse blockUnblockUser(UUID userId, GeneralStatus status, UUID adminId);

	AppResponse assignRole(UUID userId, RoleType roleType, UUID adminId);

}
