package com.example.OC.service;

import com.example.OC.entity.User;
import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.network.response.UserResponse;
import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Setter
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Log4j2(topic = "UserService")
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //회원가입
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) throws IllegalStateException{
        boolean existsByEmail = userRepository.existsByEmail(request.getEmail());
        if(existsByEmail){
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
        User saveUser = userRepository.save(user);
        SignUpResponse signUpResponse = SignUpResponse.builder()
                .email(saveUser.getEmail())
                .name(saveUser.getName())
                .build();
        return signUpResponse;
    }
    //닉네임 중복확인
    public boolean existsByNickname(String nickname){
        return userRepository.existsByNickname(nickname);
    }
    //마이페이지 조회
    public UserResponse mypage(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

}
