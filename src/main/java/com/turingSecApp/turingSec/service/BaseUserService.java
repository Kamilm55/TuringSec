package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.model.repository.BaseUserRepository;
import com.turingSecApp.turingSec.response.BaseUserDTO;
import com.turingSecApp.turingSec.service.interfaces.IBaseUserService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.BaseUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseUserService implements IBaseUserService {
    private final UserFactory userFactory;
    private final UtilService utilService;
    private final BaseUserRepository baseUserRepository;

    @Override
    public BaseUserDTO getCurrentUser() {
        return BaseUserMapper.INSTANCE.toDTO(userFactory.getAuthenticatedBaseUser());
    }

    @Override
    public BaseUserDTO getBaseUserById(String baseUserId) {
       return BaseUserMapper.INSTANCE.toDTO(utilService.findBaseUserById(baseUserId));
    }

    @Override
    @Transactional// This annotation ensures that the method is executed within a transactional context, allowing database operations like deletion to be performed reliably.
    public void deleteCurrentBaseUser() {
        // Get the authenticated user's username from the security context
        BaseUser authenticatedBaseUser = userFactory.getAuthenticatedBaseUser();

        // Delete the user
        baseUserRepository.delete(authenticatedBaseUser);
    }
}
