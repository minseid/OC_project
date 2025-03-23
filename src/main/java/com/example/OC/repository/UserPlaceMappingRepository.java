package com.example.OC.repository;

import com.example.OC.entity.Place;
import com.example.OC.entity.User;
import com.example.OC.entity.UserPlaceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlaceMappingRepository extends JpaRepository<UserPlaceMapping, Long> {

    List<UserPlaceMapping> findAllByPlace(Place place);

    Optional<UserPlaceMapping> findByUserAndPlace(User user, Place place);
}
