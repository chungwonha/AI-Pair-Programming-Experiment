package com.cool.app.newssentimentanalysis.repository.openai;

import com.cool.app.newssentimentanalysis.entity.openai.ChatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatResponseRepository extends JpaRepository<ChatResponse, String> {
    Optional<ChatResponse> findByUniqueIdentifier(String uniqueIdentifier);
}
