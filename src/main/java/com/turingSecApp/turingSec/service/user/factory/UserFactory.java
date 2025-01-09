package com.turingSecApp.turingSec.service.user.factory;

import com.turingSecApp.turingSec.config.websocket.security.CustomWebsocketSecurityContext;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.repository.BaseUserRepository;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.model.repository.UserRepository;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFactory {
    private final BaseUserRepository baseUserRepository;
    private final CustomWebsocketSecurityContext websocketSecurityContext;
    private final UtilService utilService;
    private final UserRepository userRepository;
    private final HackerRepository hackerRepository;

    // Method to retrieve authenticated BaseUser
    public BaseUser getAuthenticatedBaseUser() {
        Authentication authentication = getAuthentication();
        return getAuthenticatedBaseUser(authentication);
    }

    private Authentication getAuthentication() {
        // Determine if the request is a WebSocket request
        if (isWebSocketRequest()) {
            log.info("It is a WebSocket request");
            return websocketSecurityContext.getAuthentication();
        } else {
            log.info("It is a HTTP request");
            return SecurityContextHolder.getContext().getAuthentication();
        }
    }

    private boolean isWebSocketRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // If the RequestAttributes is null, it’s likely a WebSocket request, as HTTP requests are typically associated with attributes
        return requestAttributes == null;
    }

    private BaseUser getAuthenticatedBaseUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String baseUserId = authentication.getName();
            return baseUserRepository.findById(utilService.convertToUUID(baseUserId))
                    .orElseThrow(() -> new UserNotFoundException("BaseUser with baseUserId " + baseUserId + " not found"));
        } else {
            throw new UnauthorizedException();
        }
    }

    public HackerEntity getAuthenticatedHacker() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String baseUserId = authentication.getName();
            return hackerRepository.findByUserId(utilService.convertToUUID(baseUserId))
                    .orElseThrow(() -> new UserNotFoundException("BaseUser with baseUserId " + baseUserId + " not found"));
        } else {
            throw new UnauthorizedException();
        }
    }

}