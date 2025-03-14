package com.example.OC.repository;

import com.example.OC.entity.Inquiry;
import com.example.OC.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findAllByUser (User user);

    List<Inquiry> findAllByAnswered (Boolean answered);
}
