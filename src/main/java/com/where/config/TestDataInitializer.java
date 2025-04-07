package com.where.config;

import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class TestDataInitializer {
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            User testUser = User.builder()
                    .email("test@example.com")
                    .password(passwordEncoder.encode("Test@1234"))
                    .nickName("테스트유저")
                    .role(UserRole.USER)
                    .build();

            if (userRepository.findByEmail(testUser.getEmail()).isEmpty()) {
                userRepository.save(testUser);
                System.out.println("테스트 유저가 DB에 삽입되었습니다: " + testUser);
            } else {
                System.out.println("테스트 유저가 이미 존재합니다.");
            }
        };
    }
//
//    @Bean
//    public CommandLineRunner initMeetingDatabase(MeetingRepository meetingRepository, UserRepository userRepository, UserMeetingMappingRepository userMeetingMappingRepository) {
//        return args -> {
//            Meeting testMeeting = Meeting.builder()
//                    .title("모임1")
//                    .description("모임설명")
//                    .link("링크")
//                    .finished(false)
//                    .image("")
//                    .build();
//
//            if (meetingRepository.existsByLink(testMeeting.getLink())) {
//                System.out.println("테스트모임이 이미 있습니다.");
//            } else {
//                meetingRepository.save(testMeeting);
//                System.out.println("테스트모임생성완료");
//            }
//            UserMeetingMapping mapping = UserMeetingMapping.builder()
//                    .user(userRepository.findById(1L).get())
//                    .meeting(meetingRepository.findById(1L).get())
//                    .build();
//            if(userMeetingMappingRepository.existsByUserAndMeeting(userRepository.findById(1L).get(),meetingRepository.findById(1L).get())) {
//                System.out.println("구성원정보가 이미 있습니다");
//            } else {
//                userMeetingMappingRepository.save(mapping);
//                System.out.println("구성원정보 저장 완료");
//            }
//        };
//    }
}
