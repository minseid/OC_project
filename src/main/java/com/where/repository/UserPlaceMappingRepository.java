package com.where.repository;

import com.where.entity.Place;
import com.where.entity.User;
import com.where.entity.UserPlaceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlaceMappingRepository extends JpaRepository<UserPlaceMapping, Long> {

    List<UserPlaceMapping> findAllByPlace(Place place);

    Optional<UserPlaceMapping> findByUserAndPlace(User user, Place place);

    boolean existsByUserAndPlace(User user, Place place);

    void deleteAllByPlace(Place place);
}
