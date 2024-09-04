package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;

public interface IBaseUserService {
    BaseUser getCurrentUser();
    BaseUser getBaseUserById(String baseUserId);
    void deleteCurrentBaseUser();
}
