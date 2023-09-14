package com.kcell.testtask.messaging.repository;

import com.kcell.testtask.messaging.model.Token;
import com.kcell.testtask.messaging.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllValidTokenByUser(User user);

    Optional<Token> findByToken(String token);
}
