package com.example.OC.service;

import com.example.OC.constant.PlaceStatus;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Place;
import com.example.OC.entity.User;
import com.example.OC.repository.LinkRepository;
import com.example.OC.repository.PlaceRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@ToString
@Builder
@Transactional
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final LinkRepository linkRepository;

    public Place addPlace(Meeting meeting, User user, String name, String address) {
        Optional<Place> target = placeRepository.findByMeetingAndNameAndAddress(meeting, name, address);
        if(target.isPresent())
        {
            List<User> users = target.get().getUser();
            if(users.contains(user))
            {
                throw new IllegalArgumentException("중복된 장소");
            }
            users.add(user);
            return placeRepository.save(Place.builder()
                            .id(target.get().getId())
                            .name(name)
                            .meeting(meeting)
                            .user(users)
                            .name(name)
                            .address(address)
                            .like_count(target.get().getLike_count())
                            .placeStatus(target.get().getPlaceStatus())
                            .build()
           );
        }else{
            List<User> users = new ArrayList<>();
            users.add(user);
            //여기에 링크생성하는 코드 넣어야됨
            return placeRepository.save(Place.builder()
                    .meeting(meeting)
                    .user(users)
                    .name(name)
                    .address(address)
                    .like_count(0)
                    .placeStatus(PlaceStatus.NotPicked)
                    .build());
        }
    }
}
