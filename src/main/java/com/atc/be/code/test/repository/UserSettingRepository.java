package com.atc.be.code.test.repository;

import com.atc.be.code.test.domain.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    @Query("select m from UserSetting m WHERE m.userId.id = ?1 ")
    List<UserSetting> findByIdUser(Long id);
}
