package com.example.OC.controller;

import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.UserRepository;
import com.example.OC.service.MeetingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@Transactional
class MeetingControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserController userController;

    @Autowired
    private MeetingController meetingController;
    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    void newUserData() {
        for (int i = 1; i<10; i++){
            System.out.println("유저생성 : " + userController.signup(SignUpRequest.builder()
                    .email("testuser" + i + "@example.com")
                    .password("Test" + i + "@1234")
                    .nickName("테스트유저" + i)
                    .profileImage("https://example.com/profile/testuser" + i +".jpg")
                    .build()).toString());
        }
    }

    @Test
    void newMeetingData() {
        for (int i = 1; i<10; i++){
            meetingController.addMeeting(AddMeetingRequest.builder()
                    .title("모임" + i)
                    .description("모임설명" + i)
                    .build(),null);
        }
        System.out.println("테스트모임생성 완료");
    }

    @Test
    void addMeeting() {
        newUserData();
        ResponseEntity<AddMeetingResponse> response = meetingController.addMeeting(AddMeetingRequest.builder()
                .title("모임1")
                .fromId(Long.valueOf(1))
                .description("모임설명1")
                .participants(null)
                .build(),null);
        assert(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
    }

    @Test
    void errorAddMeeting() {
        newUserData();
        //아직 저장되어있지 않은 유저id를 대입
        ResponseEntity<AddMeetingResponse> response = meetingController.addMeeting(AddMeetingRequest.builder()
                .title("모임1")
                .fromId(Long.valueOf(20))
                .description("모임설명1")
                .participants(null)
                .build(),null);
        assert(response.getStatusCode().is4xxClientError());
        System.out.println("유저id오류 : " + response.getBody());

        //특정 필수 데이터를 미대입
        ResponseEntity<AddMeetingResponse> response2 = meetingController.addMeeting(AddMeetingRequest.builder()
                .title(null)
                .fromId(Long.valueOf(20))
                .description("모임설명1")
                .participants(null)
                .build(),null);
        assert(response.getStatusCode().is4xxClientError());
        System.out.println(response.getBody());
    }

    @Test
    void editMeeting() {
        newMeetingData();
    }

    @Test
    void quitMeeting() {
    }

    @Test
    void getParticipants() {
    }

    @Test
    void getMeetings() {
    }

    @Test
    void inviteMeeting() {
    }

    @Test
    void inviteOk() {
    }
}