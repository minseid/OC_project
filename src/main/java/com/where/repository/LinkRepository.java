package com.where.repository;

import com.where.entity.Link;
import com.where.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByPlace (Place place);

    void deleteByPlace(Place place);
}
