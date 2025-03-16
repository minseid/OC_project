package com.example.OC.repository;

import com.example.OC.constant.NoticeStatus;
import com.example.OC.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByNoticeStatus(NoticeStatus noticeStatus);
}
