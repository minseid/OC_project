package com.where.repository;

import com.where.constant.NoticeStatus;
import com.where.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByNoticeStatus(NoticeStatus noticeStatus);
}
