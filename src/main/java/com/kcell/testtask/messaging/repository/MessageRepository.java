package com.kcell.testtask.messaging.repository;

import com.kcell.testtask.messaging.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
     @Query(
             value = "INSERT INTO messages (user_id, content, created_at) VALUES (:userId, :content, :date)",
             nativeQuery = true)
     @Modifying
     @Transactional
     int addMessage(Long userId, String content, Timestamp date);

     List<Message> findAllBy();
}
