package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;

public interface UserMgmtService {

    public UserRegistrationResponseDTO registerNewUser(UserRegistrationRequestDTO userReqDTO);

    public  boolean isNewUser(String userName);
}
