package com.cool.app.newssentimentanalysis.repository.openai;

import com.cool.app.newssentimentanalysis.entity.openai.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {}
