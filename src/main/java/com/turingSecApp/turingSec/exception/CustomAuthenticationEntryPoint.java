package com.turingSecApp.turingSec.exception;

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
            System.out.println(response.getStatus() + " error occurred in CustomAuthenticationEntryPoint !");
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR: CustomAuthenticationEntryPoint",authException.getMessage());
        }

        System.out.println(authException.getMessage());
        System.out.println("CustomAuthenticationEntryPoint RestControllerAdvice works");
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

