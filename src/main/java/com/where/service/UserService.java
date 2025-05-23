package com.where.service;

import com.where.constant.EntityType;
import com.where.constant.ImageType;
import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.network.request.SignUpRequest;
import com.where.network.response.CommonProfileImageResponse;
import com.where.network.response.SignUpResponse;
import com.where.network.response.UserResponse;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Log4j2(topic = "UserService")
public class UserService {
    private final KakaoService kakaoService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder instead of BCryptPasswordEncoder
    private final FindService findService;
    private final AwsS3Service awsS3Service;

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
                .role(UserRole.USER) // 기본 역할 설정
                .build();
        System.out.println("빌드완료 : " + user.toString());

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Log the successful sign-up
        log.info("New user signed up: {}", savedUser.getEmail());

        // Return response with email and name
        return SignUpResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .nickName(savedUser.getNickName())
                .build();
    }
    //이메일 중복확인
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
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
                .id(user.getId())
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

    // UserService 클래스 내
    public void updateFcmToken(Long userId, String newToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(newToken);
        userRepository.save(user);
    }

    //닉네임변경
    public void editNickName(Long userId, String nickName) {
        User user = findService.valid(userRepository.findById(userId), EntityType.User);
        user.setNickName(nickName);
        userRepository.save(user);
    }

    //프로필이미지설정
    public CommonProfileImageResponse setProfileImage(Long userId, MultipartFile profileImage) {
        User user = findService.valid(userRepository.findById(userId), EntityType.User);
        String link = awsS3Service.saveProfileImage(profileImage, userId);
        user.setProfileImage(link);
        userRepository.save(user);
        return CommonProfileImageResponse.builder().newLink(link).build();
    }

    //프로필이미지변경
    public CommonProfileImageResponse editProfileImage(Long userId, MultipartFile profileImage) {
        User user = findService.valid(userRepository.findById(userId), EntityType.User);
        if(user.getProfileImage()!=null && user.getProfileImage().contains("where-bucket32.s3.ap-northeast-2.amazonaws.com")) {
            String newLink = awsS3Service.editProfileImage(profileImage,userId, user.getProfileImage());
            user.setProfileImage(newLink);
            userRepository.save(user);
            return CommonProfileImageResponse.builder()
                    .newLink(newLink)
                    .build();
        } else if (user.getProfileImage()!=null) {
            String newLink = awsS3Service.saveProfileImage(profileImage,userId);
            user.setProfileImage(newLink);
            userRepository.save(user);
            return CommonProfileImageResponse.builder()
                    .newLink(newLink)
                    .build();
        } else {
            throw new IllegalArgumentException("기본프로필이미지 사용중입니다!");
        }
    }

    //프로필이미지삭제
    public void deleteProfileImage(Long userId) {
        User user = findService.valid(userRepository.findById(userId), EntityType.User);
        if(user.getProfileImage() != null && user.getProfileImage().contains("where-bucket32.s3.ap-northeast-2.amazonaws.com")) {
            awsS3Service.delete(user.getProfileImage(), ImageType.Profile);
            user.setProfileImage(null);
            userRepository.save(user);
        } else if (user.getProfileImage() != null) {
            user.setProfileImage(null);
            userRepository.save(user);
        } else {
            return;
        }
    }

    public String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
