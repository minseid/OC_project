package com.where.repository;

import com.where.entity.Inquiry;
import com.where.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findAllByUser (User user);

    List<Inquiry> findAllByAnswered (Boolean answered);
}
