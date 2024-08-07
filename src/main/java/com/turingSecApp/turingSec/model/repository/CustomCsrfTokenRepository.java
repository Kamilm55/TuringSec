package com.turingSecApp.turingSec.model.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

@Component
public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    private final String staticToken = "DQsImV4cCI6MTcyMzI5NTEwNH0"; // Static token for simulation

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        // Return a static CSRF token
        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", staticToken);
    }

    public CsrfToken generateToken() {
        // Return a static CSRF token
        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", staticToken);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        // Save the CSRF token to the session
        HttpSession session = request.getSession();
        if (token == null) {
            session.removeAttribute("CSRF_TOKEN");
        } else {
            session.setAttribute("CSRF_TOKEN", token);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        // Load the CSRF token from the db
//        HttpSession session = request.getSession(false);
        return /*(session != null) ? (CsrfToken) session.getAttribute("CSRF_TOKEN") : */generateToken(request);
    }
    public CsrfToken loadToken() {
        // Load the CSRF token from the db
        return generateToken();
    }
}
