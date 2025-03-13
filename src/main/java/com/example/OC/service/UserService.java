package com.example.OC.service;

import com.example.OC.entity.User;
import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.network.response.UserResponse;
import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2(topic = "UserService")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder instead of BCryptPasswordEncoder

    //회원가입
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        // Check if email is already taken
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());
        if (existsByEmail) {
            log.error("Email already exists: {}", request.getEmail());
            throw new IllegalStateException("이미 가입된 이메일입니다."); // Email already exists
        }

        // Check if nickname is already taken
        boolean existsByNickName = userRepository.existsByNickName(request.getNickName());
        if (existsByNickName) {
            log.error("NickName already exists: {}", request.getNickName());
            throw new IllegalStateException("이미 사용 중인 닉네임입니다."); // Nickname already exists
        }

        // Encrypt password using BCrypt (via PasswordEncoder)
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User entity
        User user = User.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickName(request.getNickName()) // Assuming you are using nickname
                .build();

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Log the successful sign-up
        log.info("New user signed up: {}", savedUser.getEmail());

        // Return response with email and name
        return SignUpResponse.builder()
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .nickName(savedUser.getNickName())
                .build();
    }

    //닉네임 중복확인
    public boolean existsByNickName(String nickName) {
        boolean exists = userRepository.existsByNickName(nickName);
        if (exists) {
            log.warn("Nickname already exists: {}", nickName);
        }
        return exists;
    }

    //마이페이지 조회
    public UserResponse mypage(String userId) {
        // Find user by ID
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new IllegalArgumentException("해당 유저가 존재하지 않습니다."); // User not found
                });

        // Log the successful retrieval of user details
        log.info("User details fetched for: {}", user.getEmail());

        // Return user details in the response format
        return UserResponse.builder()
                .email(user.getEmail())
                .nickName(user.getNickName()) // Assuming you have a nickname
                .profileImage(user.getProfileImage()) // Assuming you have profile image
                .build();
    }

    // 로그인 시도 시 비밀번호 비교 (optional method)
    public boolean verifyPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
    //탈퇴하기
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(String userId) {
        // Find user by ID
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                     log.error("User not found: {}", userId);
                     throw new IllegalArgumentException("해당 유저가 존재하지 않습니다."); // User not found
                 });

        // Delete user from the database
        userRepository.delete(user);

        // Log the successful deletion of user
        log.info("User deleted: {}", user.getEmail());
    }
}
