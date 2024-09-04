package com.turingSecApp.turingSec.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (response.getStatus() == 200) { // Force to 401
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required. Please provide valid credentials.");
        } else if (response.getStatus() == 403) {
            setErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to access this resource.");
        }
        else {
            if(authException.getClass().getSimpleName().equals("InsufficientAuthenticationException")){
                setErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden", "Authentication required. Please provide valid credentials (Ensure your TOKEN IS VALID for authorized requests, otherwise if it is register or login or any method is anonymous you must send request without token, get token after successful login). Exception in filter -> InsufficientAuthenticationException!");
            }else {
                log.error(response.getStatus() + " error occurred in CustomAuthenticationEntryPoint!");
                setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR: CustomAuthenticationEntryPoint",authException.getMessage());
            }
        }

        log.error(authException.getClass().getName());
        log.error("authException message: " + authException.getMessage());
        log.error("CustomAuthenticationEntryPoint RestControllerAdvice works");
    }
    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String key = "Security error: CustomAuthenticationEntryPoint";
        PrintWriter writer = response.getWriter();
        writer.println("{");
        writer.println("  \"key\": \"" + key + "\",");
        writer.println("  \"message\": \"" + message + "\",");
        writer.println("  \"status\": \"" + error + "\"");
        writer.println("}");
        writer.flush();
    }


}

