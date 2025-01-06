package com.example.side.auth;

import com.example.side.user.dto.request.UserDto;
import com.example.side.user.entity.User;
import com.example.side.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUid(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "(을)를 찾을 수 없습니다."));

//        UserDto userDto = new UserDto(user.getUsername(), user.getPassword(), user.getRole().getKey());

        return new CustomUserDetails(user);
    }
}
