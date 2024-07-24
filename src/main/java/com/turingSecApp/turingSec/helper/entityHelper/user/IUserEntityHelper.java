package com.turingSecApp.turingSec.helper.entityHelper.user;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntityI;
import com.turingSecApp.turingSec.payload.user.ChangeEmailRequest;
import com.turingSecApp.turingSec.payload.user.ChangePasswordRequest;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.payload.user.UserUpdateRequest;

public interface IUserEntityHelper {
    UserEntityI createUserEntity(RegisterPayload registerPayload, boolean activated);
    void setHackerInUserEntity(UserEntityI user, HackerEntity hackerEntity);
    void setUserInHackerEntity(UserEntityI user, HackerEntity hackerEntity);

    HackerEntity createHackerEntity(UserEntityI user);

    UserEntityI findByEmail(String email);

    UserEntityI findUserByUsername(String usernameOrEmail);

    UserEntityI findUserById(Long id);

    HackerEntity findHackerByUser(UserEntityI userById);

    void validateCurrentPassword(ChangePasswordRequest request, UserEntityI user);

    public void validateCurrentPassword(ChangeEmailRequest request, UserEntityI user);

    void updatePassword(String newPassword, String confirmNewPassword, UserEntityI user);

    void checkIfEmailExists(String newEmail);

    void updateUserProfile(UserEntityI userEntity, UserUpdateRequest userUpdateRequest);

    void updateHackerProfile(HackerEntity hackerEntity, UserUpdateRequest userUpdateRequest);
}
