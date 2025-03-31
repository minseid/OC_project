package com.example.OC.repository;

import com.example.OC.entity.Link;
import com.example.OC.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByPlace (Place place);

    void deleteByPlace(Place place);
}
