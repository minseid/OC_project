package com.example.OC.repository;

import com.example.OC.entity.Comment;
import com.example.OC.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPlace(Place place);
}
