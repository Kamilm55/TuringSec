package com.turingSecApp.turingSec.config;


import com.turingSecApp.turingSec.exception.CustomAuthenticationEntryPoint;
import com.turingSecApp.turingSec.filter.JwtAuthenticationFilter;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.enums.Role;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.ws.rs.GET;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Apply CSRF protection selectively to STOMP endpoints
//                .csrf(  csrf -> csrf
//                    .requireCsrfProtectionMatcher(request -> request.getServletPath().startsWith("/ws"))
//                )
                .headers(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) ->
                        exception.authenticationEntryPoint(authenticationEntryPoint())
                )
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers("/api/locations/**").hasAnyRole("HACKER");

                    // Spring Security evaluates matchers in the order they are defined,
                    // whichever matcher appears first will take precedence.
                    // Therefore, if the authenticated() matcher comes first, the permitAll() matcher for specific paths will be ignored

                    // More specific path -> to general path (order of security config)

                    // Spring Security uses Ant-style path matching, where "/api/base-users/{baseUserId}" is treated as a pattern with a placeholder ({baseUserId}), often matching any single segment within that path (e.g., /api/base-users/123).
                    // The pattern "/api/base-users/current-user" is a specific literal match. It matches exactly and only /api/base-users/current-user
                    request.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();

                    // CSRF controller
                    request.requestMatchers("/api/csrf/**").authenticated();

                    //CORS and preflight
                    request.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Socket
                    request.requestMatchers("/ws/**").permitAll();

                    // Media controller
                    request.requestMatchers("/api/background-image-for-hacker/**", "/api/background-image-for-company/**", "/api/image-for-hacker/**", "/api/image-for-company/**", "/api/report-media/**").permitAll();//.authenticated();

                    // h2 console
                    request.requestMatchers("/h2-console/**").permitAll(); // permits access to all URLs starting with /h2-console/ without authentication.

                    // test
                    request.requestMatchers("/api/test/**").permitAll();

                    // Bug Bounty Program Controller
                    request
                            .requestMatchers("/api/bug-bounty-programs").hasRole("COMPANY")
                            .requestMatchers(HttpMethod.DELETE, "/api/bug-bounty-programs/**").hasRole("COMPANY")
                            .requestMatchers("/api/bug-bounty-programs/**").permitAll();


                    // Base User Controller
                    request
                            .requestMatchers("/api/base-users/current-user").authenticated()
                            .requestMatchers("/api/base-users/{baseUserId}").permitAll()
                            .requestMatchers("/api/base-users/**").authenticated()
                    ;


                    // User Controller
                    request
                            .requestMatchers("/api/auth/register/hacker").anonymous() // anonymous means , only unauthenticated users can send request
                            .requestMatchers("/api/auth/login").anonymous() // Public endpoints for registration and login

                            .requestMatchers("/api/auth/change-password").authenticated()
                            .requestMatchers("/api/auth/change-email").authenticated()
                            .requestMatchers("/api/auth/update-profile").authenticated()
                            .requestMatchers("/api/auth/test").authenticated()
                            .requestMatchers("/api/auth/current-user").authenticated()
                            .requestMatchers("/api/auth/allUsers").permitAll()
                            .requestMatchers("/api/auth/activate").permitAll()
                            .requestMatchers("/api/auth/users/{userId}").authenticated()
                            .requestMatchers("/api/auth/delete-user").authenticated()
                            .requestMatchers("/api/auth/programs").hasRole("HACKER")// ALL PROGRAMS
                            .requestMatchers("/api/auth/programsById/{id}").hasRole("HACKER")
                            .requestMatchers("/api/auth/users/**").permitAll();

                    // Company Controller
                    request
                            .requestMatchers("/api/companies/register").anonymous()
                            .requestMatchers("/api/companies/login").anonymous()
                            .requestMatchers("/api/companies/current-user").hasRole("COMPANY") // only company
                            .requestMatchers("/api/companies/**").permitAll();

                    // Hacker Controller
                    request.requestMatchers("/api/hacker/{id}").permitAll();

                    // Admin Controller
                    request
                            .requestMatchers("/api/admin/login").anonymous()
                            .requestMatchers("/api/admin/approve-company/{companyId}").hasRole("ADMIN");


                    // Bug Bounty Report Controller
                    request


                            .requestMatchers(HttpMethod.POST,"/api/bug-bounty-reports/**").authenticated()
                            .requestMatchers(HttpMethod.PUT,"/api/bug-bounty-reports/**").authenticated()

                            .requestMatchers("/api/bug-bounty-reports/user").hasRole("HACKER")
                            .requestMatchers("/api/bug-bounty-reports/company").hasRole("COMPANY")
                            .requestMatchers("/api/bug-bounty-reports/*/company/**").authenticated()

                            .requestMatchers("/api/bug-bounty-reports/company/{id}/admin").hasRole("ADMIN")
                            .requestMatchers("/api/bug-bounty-reports/user/{id}/admin").hasRole("ADMIN")
                            .requestMatchers("/api/bug-bounty-reports/admin").hasRole("ADMIN")
                            .requestMatchers("/api/bug-bounty-reports/date-range").hasRole("ADMIN")
                            .requestMatchers("/api/bug-bounty-reports/date-range/company").hasRole(Role.ROLE_COMPANY.getValue())
                            .requestMatchers("/api/bug-bounty-reports/date-range/user").hasRole(Role.ROLE_HACKER.getValue())
                            .requestMatchers("/api/bug-bounty-reports/{id}").authenticated()
                            .requestMatchers("/api/bug-bounty-reports/**").authenticated()
                    ;

                    // Notification

                    request.requestMatchers(HttpMethod.OPTIONS, "/api/**").hasRole("HACKER")

                    request
                            .requestMatchers("/api/notification").hasRole("HACKER")
                            .requestMatchers("/api/sse/notifications").hasRole("HACKER");

                    // Message in Report Controller
                    request
                            .requestMatchers("/api/messagesInReport").hasAnyRole("HACKER", "COMPANY")
                            .requestMatchers("/api/messagesInReport/{id}").hasAnyRole("HACKER", "COMPANY")
                            .requestMatchers("/api/messagesInReport/report/{id}/admin").hasRole("ADMIN")
                            .requestMatchers("/api/messagesInReport/message/{id}/admin").hasRole("ADMIN");

                    // Card Controller
                    request
                            .requestMatchers("/api/cards/**").hasRole("HACKER");
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
