package com.turingSecApp.turingSec.helper.entityHelper.user;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.payload.user.ChangeEmailRequest;
import com.turingSecApp.turingSec.payload.user.ChangePasswordRequest;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.payload.user.UserUpdateRequest;

public interface IUserEntityHelper {
    UserEntity createUserEntity(RegisterPayload registerPayload, boolean activated);
    void setHackerInUserEntity(UserEntity user, HackerEntity hackerEntity);
    void setUserInHackerEntity(UserEntity user, HackerEntity hackerEntity);

    HackerEntity createHackerEntity(UserEntity user);

    UserEntity findByEmail(String email);

    UserEntity findUserByUsername(String usernameOrEmail);

    UserEntity findUserById(Long id);

    HackerEntity findHackerByUser(UserEntity userById);

    void validateCurrentPassword(ChangePasswordRequest request, UserEntity user);

    public void validateCurrentPassword(ChangeEmailRequest request, UserEntity user);

    void updatePassword(String newPassword, String confirmNewPassword, UserEntity user);

    void checkIfEmailExists(String newEmail);

    void updateUserProfile(UserEntity userEntity, UserUpdateRequest userUpdateRequest);

    void updateHackerProfile(HackerEntity hackerEntity, UserUpdateRequest userUpdateRequest);
}
