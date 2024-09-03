package com.turingSecApp.turingSec.service.user;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Slf4j
public class CustomUserDetails implements UserDetails {

    private final BaseUser user;

    public CustomUserDetails(BaseUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("USER TYPE: " + user);
        return user.getRoles();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // In Company there is no username
        // Instead I use UUID as string which is common unique field for all user entities
        // todo: change all uuid getter setter to string
        return user.getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement as needed
    }
    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement as needed
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement as needed
    }
    @Override
    public boolean isEnabled() {
        return true; // Implement as needed
    }
    public Object getUser() {
        return user;
    }

    public String getId() {
        return user.getId();
    }
}