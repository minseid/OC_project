package com.example.OC.repository;

import com.example.OC.entity.Comment_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Comment_Repository extends JpaRepository<Comment_Entity, Long> {

}
