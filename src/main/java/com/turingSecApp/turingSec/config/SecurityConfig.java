package com.turingSecApp.turingSec.config;


import com.turingSecApp.turingSec.exception.CustomAuthenticationEntryPoint;
import com.turingSecApp.turingSec.filter.JwtAuthenticationFilter;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http , JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling( (exception)->
                        exception.authenticationEntryPoint(authenticationEntryPoint())
                )
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();

                    // Socket IO
                    request.requestMatchers("/ws/**").authenticated();

                    // Media controller
                    request.requestMatchers("/api/background-image-for-hacker/**", "/api/image-for-hacker/**", "/api/report-media/**").permitAll();//.authenticated();

                    // h2 console
                    request.requestMatchers("/h2-console/**").permitAll(); // permits access to all URLs starting with /h2-console/ without authentication.

                    // test
                    request.requestMatchers("/api/test").permitAll();

                    // Bug Bounty Program Controller
                    request
                            .requestMatchers("/api/bug-bounty-programs").hasRole("COMPANY")
                            .requestMatchers(HttpMethod.DELETE,"/api/bug-bounty-programs/**").hasRole("COMPANY")
                            .requestMatchers("/api/bug-bounty-programs/**").permitAll();

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
                            .requestMatchers("/api/bug-bounty-reports/reports/company").hasRole("COMPANY")
                            .requestMatchers(HttpMethod.POST,"/api/bug-bounty-reports/**").hasRole("HACKER")
                            .requestMatchers(HttpMethod.PUT,"/api/bug-bounty-reports/**").hasRole("HACKER")
                            .requestMatchers("/api/bug-bounty-reports/submit").hasRole("HACKER")
                            .requestMatchers("/api/bug-bounty-reports/user").hasRole("HACKER")


                            .requestMatchers(HttpMethod.GET , "/api/bug-bounty-reports/{id}").authenticated()

                            .requestMatchers("/api/bug-bounty-reports/{id}").hasRole("HACKER");


                })
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
