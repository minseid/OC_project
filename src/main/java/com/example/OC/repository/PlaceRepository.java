package com.example.OC.repository;

import com.example.OC.constant.PlaceStatus;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByMeetingAndNameAndAddress(Meeting meeting, String name, String address);

    List<Place> findAllByXAndY(float x, float y);

    List<Place> findAllByPlaceStatus(PlaceStatus placeStatus);

}
