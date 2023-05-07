package com.atc.be.code.test.repository;

import com.atc.be.code.test.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.id LIMIT ?1 OFFSET ?2")
    List<User> findUserWithLimitAndOffset(int limit, int offset);
}
