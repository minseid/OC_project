package com.example.OC;

import com.example.OC.entity.User;
import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.repository.UserRepository;
import com.example.OC.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Mocked Repository

    @Mock
    private PasswordEncoder passwordEncoder; // Mocked PasswordEncoder

    @InjectMocks
    private UserService userService; // 실제 서비스에 Mock 주입

    @Test
    void signUp_ShouldCreateNewUser_WhenValidRequest() {
        // Given: Mock 데이터 준비
        SignUpRequest request = SignUpRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickName("testNick")
                .build();

        User mockUser = User.builder()
                .email(request.getEmail())
                .password("encryptedPassword123")
                .nickName(request.getNickName())
                .build();

        // Mock 동작 정의
        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByNickName(request.getNickName())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword123");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

        // When: 서비스 호출
        SignUpResponse response = userService.signUp(request);

        // Then: 결과 검증
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getEmail(), request.getEmail());
        Assertions.assertEquals(response.getNickName(), request.getNickName());

        // Verify: Mock이 올바르게 호출되었는지 확인
        Mockito.verify(userRepository).existsByEmail(request.getEmail());
        Mockito.verify(userRepository).existsByNickName(request.getNickName());
        Mockito.verify(passwordEncoder).encode(request.getPassword());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void signUp_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given: Mock 데이터 준비
        SignUpRequest request = SignUpRequest.builder()
                .email("existing@example.com")
                .password("password123")
                .nickName("testNick")
                .build();

        // Mock 동작 정의 (이메일 중복)
        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then: 예외 발생 확인
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            userService.signUp(request);
        });

        Assertions.assertEquals("이미 가입된 이메일입니다.", exception.getMessage());

        // Verify: Mock이 올바르게 호출되었는지 확인
        Mockito.verify(userRepository).existsByEmail(request.getEmail());
    }

    @Test
    void signUp_ShouldThrowException_WhenNickNameAlreadyExists() {
        // Given: Mock 데이터 준비
        SignUpRequest request = SignUpRequest.builder()
                .email("new@example.com")
                .password("password123")
                .nickName("existingNick")
                .build();

        // Mock 동작 정의 (닉네임 중복)
        Mockito.when(userRepository.existsByNickName(request.getNickName())).thenReturn(true);

        // When & Then: 예외 발생 확인
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            userService.signUp(request);
        });

        Assertions.assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());

        // Verify: Mock이 올바르게 호출되었는지 확인
        Mockito.verify(userRepository).existsByNickName(request.getNickName());
    }
}
