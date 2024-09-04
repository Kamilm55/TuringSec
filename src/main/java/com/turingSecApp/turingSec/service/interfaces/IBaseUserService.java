package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.response.BaseUserDTO;

public interface IBaseUserService {
    BaseUserDTO getCurrentUser();
    BaseUserDTO getBaseUserById(String baseUserId);
    void deleteCurrentBaseUser();
}
