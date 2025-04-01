package com.where.repository;

import com.where.constant.PlaceStatus;
import com.where.entity.Meeting;
import com.where.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByMeetingAndNameAndAddress(Meeting meeting, String name, String address);

    List<Place> findAllByXAndYAndMeeting(float x, float y, Meeting meeting);

    List<Place> findAllByPlaceStatus(PlaceStatus placeStatus);

    List<Place> findAllByMeeting(Meeting meeting);

    boolean existsByMeeting(Meeting meeting);
}
