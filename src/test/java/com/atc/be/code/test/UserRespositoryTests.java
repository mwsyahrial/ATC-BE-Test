package com.atc.be.code.test;

import com.atc.be.code.test.domain.User;
import com.atc.be.code.test.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    void saveEmployeeTest(){
        Date date = new Date();
        LocalDateTime datetime = LocalDateTime.now();
        User user = User.builder()
                .ssn("0000000000002947")
                .firstName("Jon")
                .familyName("Snow")
                .birthDate(date)
                .createdTime(datetime)
                .updateTime(datetime)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .isActive(true)
                .build();

        userRepository.save(user);

        Assertions.assertThat(user.getId()).isPositive();
    }

    @Test
    @Order(2)
    void getUserTest(){

        User user = userRepository.findById(1L).get();

        Assertions.assertThat(user.getId()).isEqualTo(1L);

    }

    @Test
    @Order(3)
    void getListOfUserTest(){

        List<User> user = userRepository.findAll();

        Assertions.assertThat(user.size()).isPositive();

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void updateUserTest(){

        User user = userRepository.findById(1L).get();

        user.setSsn("0000000000002945");

        User userUpdate =  userRepository.save(user);

        Assertions.assertThat(userUpdate.getSsn()).isEqualTo("0000000000002945");

    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void deleteUserTest(){

        User user = userRepository.findById(1L).get();

        userRepository.delete(user);

        User user1 = null;

        Optional<User> optionalUser = userRepository.findById(1L);

        if(optionalUser.isPresent()){
            user1 = optionalUser.get();
        }

        Assertions.assertThat(user1).isNull();
    }
}