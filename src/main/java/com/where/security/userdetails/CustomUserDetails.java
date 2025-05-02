package com.where.security.userdetails;

import com.where.entity.User;
import com.where.service.UserService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@EqualsAndHashCode
@Slf4j
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
        log.error("패스워드가져오기 : " + user.getPassword());
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
