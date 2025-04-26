package com.where.repository;

import com.where.entity.Comment;
import com.where.entity.Place;
import com.where.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPlace(Place place);

    void deleteAllByPlace(Place place);

    boolean existsByUser(User user);

    long countByPlace(Place place);
}
