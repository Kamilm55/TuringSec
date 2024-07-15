package com.turingSecApp.turingSec.helper.entityHelper.user;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;

public interface IUserEntityHelper {
    UserEntity createUserEntity(RegisterPayload registerPayload, boolean activated);
    void setHackerInUserEntity(UserEntity user, HackerEntity hackerEntity);
    void setUserInHackerEntity(UserEntity user, HackerEntity hackerEntity);

    HackerEntity createHackerEntity(UserEntity user);
}
