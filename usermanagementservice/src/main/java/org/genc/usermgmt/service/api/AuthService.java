package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.AuthRequestDTO;
import org.genc.usermgmt.dto.AuthResponseDTO;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO request);

    AuthResponseDTO register(UserRegistrationRequestDTO request);
}