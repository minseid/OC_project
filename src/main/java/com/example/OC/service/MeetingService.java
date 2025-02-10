package com.example.OC.service;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.ParticipantRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    boolean flag = false;
    String reception = "";

    private String makeLink(){
        while(!flag) {
            Random rnd = new Random();
            StringBuffer buf = new StringBuffer();

            for(int i=0;i<10;i++){
                if(rnd.nextBoolean()) {
                    buf.append((char)(int)(rnd.nextInt(26)+97));
                } else {
                  buf.append((rnd.nextInt(10)));
                }
            }
            String randomCode = buf.toString();
            if(meetingRepository.findByLink(randomCode).isEmpty()) {
                flag = true;
                reception = randomCode;
            } else {
                flag = false;
            }
        }
        return reception;
    }

    public Meeting addMeeting(String title, String description, String image,String fromId, List<Long> friends ) {
        Meeting target = Meeting.builder()
                .title(title)
                .description(description)
                .link(makeLink())
                .image(image==null?null:image)
                .build();
        friends.forEach(id -> participantRepository.save(Participant.builder()
                .meeting(target)
                .fromId(Long.parseLong(fromId))
                .toId(id)
                .status(false)
                .build()));
        return meetingRepository.save(target);
    }


}
