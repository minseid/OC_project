package com.example.OC.security.userdetails;

import com.example.OC.entity.User;
import com.example.OC.service.UserService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Component
@EqualsAndHashCode
public class CustomUserDetails implements UserDetails {

    private final UserService userService;
    private User user;

    public CustomUserDetails(UserService userService) {
        this.userService = userService;
    }

    public void setUserByEmail(String email) {
        this.user = userService.findUserByEmail(email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> user.getRole().getKey());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
