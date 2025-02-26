package com.example.OC.security.userdetails;

import com.example.OC.entity.User;
import com.example.OC.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final CustomUserDetails customUserDetails;

    public CustomUserDetailsService(UserService userService, CustomUserDetails customUserDetails) {
        this.userService = userService;
        this.customUserDetails = customUserDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        customUserDetails.setUserByEmail(email);
        return customUserDetails;
    }
}
