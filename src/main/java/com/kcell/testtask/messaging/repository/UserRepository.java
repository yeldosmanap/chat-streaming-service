package com.kcell.testtask.messaging.repository;

import com.kcell.testtask.messaging.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameOrEmail(String username, String email);

    User getUserById(Long id);
}
