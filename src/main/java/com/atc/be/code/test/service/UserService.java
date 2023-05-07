package com.atc.be.code.test.service;

import com.atc.be.code.test.domain.User;
import com.atc.be.code.test.domain.UserSetting;
import com.atc.be.code.test.model.request.UserRq;
import com.atc.be.code.test.model.response.UserDataRs;
import com.atc.be.code.test.model.response.UserRs;
import com.atc.be.code.test.model.response.UsersRs;
import com.atc.be.code.test.repository.UserRepository;
import com.atc.be.code.test.repository.UserSettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSettingRepository userSettingRepository;

    public UsersRs getAll(int maxRecords, int offset){
        try{
            List<User> users = userRepository.findUserWithLimitAndOffset(maxRecords, offset);
            List<UserDataRs> userDataRs = new ArrayList<>();
            if(!users.isEmpty()) {
                for (User user : users) {
                    if(user.getIsActive().equals(true)) {
                        userDataRs.add(UserDataRs.builder()
                                .id(user.getId())
                                .ssn(user.getSsn())
                                .firstName(user.getFirstName())
                                .lastName(user.getFamilyName())
                                .birthDate(user.getBirthDate().toString())
                                .createdTime(user.getCreatedTime().toString())
                                .updatedTime(user.getUpdateTime().toString())
                                .createdBy(user.getCreatedBy())
                                .updatedBy(user.getUpdatedBy())
                                .isActive(user.getIsActive())
                                .build());
                    }
                }
            }
            return UsersRs.builder()
                    .userData(userDataRs)
                    .maxRecords(maxRecords)
                    .offset(offset)
                    .build();
        } catch(Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public UserRs createUser(UserRq request) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            LocalDateTime datetime = LocalDateTime.now();
            User userNew = User.builder()
                    .ssn(request.getSsn())
                    .firstName(request.getFirstName())
                    .familyName(request.getLastName())
                    .birthDate(request.getBirthDate())
                    .createdBy("SYSTEM")
                    .updatedBy("SYSTEM")
                    .createdTime(datetime)
                    .updateTime(datetime)
                    .isActive(true)
                    .build();

            User user = userRepository.save(userNew);

            Map<String, String> userSet = new HashMap<>();

            String[] settings= {"biometric_login","push_notification","sms_notification","show_onboarding","widget_order"};
            for (int i = 0; i < settings.length; i++) {
                UserSetting uSetting = new UserSetting();
                uSetting.setUserId(user);
                uSetting.setKey(settings[i]);
                if(i == 4){
                    uSetting.setValue("1,2,3,4,5");
                    userSet.put(settings[i],"1,2,3,4,5");
                }else {
                    uSetting.setValue("false");
                    userSet.put(settings[i],"false");
                }
                userSettingRepository.save(uSetting);
            }
            UserRs response = UserRs.builder()
                    .userData(UserDataRs.builder()
                            .id(user.getId())
                            .ssn(user.getSsn())
                            .firstName(user.getFirstName())
                            .lastName(user.getFamilyName())
                            .birthDate(user.getBirthDate().toString())
                            .createdTime(user.getCreatedTime().toString())
                            .updatedTime(user.getUpdateTime().toString())
                            .createdBy(user.getCreatedBy())
                            .updatedBy(user.getUpdatedBy())
                            .isActive(user.getIsActive())
                            .build())
                    .build();

            List<Map.Entry<String, String>> listAll = new ArrayList<>(userSet.entrySet());
            response.setUserSettings(listAll);

            return response;

        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
    public UserRs getUserById(Long id) {
        try {
            Optional<User> userDb =userRepository.findById(id);
            User user = new User();
            if(userDb.isPresent()) {
                user = userDb.get();
            }
            if(user.getIsActive().equals(true)) {
                List<UserSetting> uSets = userSettingRepository.findByIdUser(id);

                Map<String, String> userSet = new HashMap<>();

                for (UserSetting uSet : uSets) {
                    userSet.put(uSet.getKey(), uSet.getValue());
                }

                List<Map.Entry<String, String>> listAll = new ArrayList<>(userSet.entrySet());


                return UserRs.builder()
                        .userData(UserDataRs.builder()
                                .id(user.getId())
                                .ssn(user.getSsn())
                                .firstName(user.getFirstName())
                                .lastName(user.getFamilyName())
                                .birthDate(user.getBirthDate().toString())
                                .createdTime(user.getCreatedTime().toString())
                                .updatedTime(user.getUpdateTime().toString())
                                .createdBy(user.getCreatedBy())
                                .updatedBy(user.getUpdatedBy())
                                .isActive(user.getIsActive())
                                .build())
                        .userSettings(listAll)
                        .build();
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public UserRs putUser(UserRq request, Long id) {
        try {
            Optional<User> userDb =userRepository.findById(id);
            User user = new User();
            if(userDb.isPresent()) {
                user = userDb.get();
            }
            if(user.getIsActive().equals(true)) {
                user.setSsn(request.getSsn());
                user.setFirstName(request.getFirstName());
                user.setFamilyName(request.getLastName());
                user.setBirthDate(request.getBirthDate());
                user.setUpdateTime(LocalDateTime.now());

                userRepository.save(user);

                List<UserSetting> uSets = userSettingRepository.findByIdUser(id);

                Map<String, String> userSet = new HashMap<>();

                for (UserSetting uSet : uSets) {
                    userSet.put(uSet.getKey(), uSet.getValue());
                }

                List<Map.Entry<String, String>> listAll = new ArrayList<>(userSet.entrySet());

                return UserRs.builder()
                        .userData(UserDataRs.builder()
                                .id(user.getId())
                                .ssn(user.getSsn())
                                .firstName(user.getFirstName())
                                .lastName(user.getFamilyName())
                                .birthDate(user.getBirthDate().toString())
                                .createdTime(user.getCreatedTime().toString())
                                .updatedTime(user.getUpdateTime().toString())
                                .createdBy(user.getCreatedBy())
                                .updatedBy(user.getUpdatedBy())
                                .isActive(user.getIsActive())
                                .build())
                        .userSettings(listAll)
                        .build();
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
    public UserRs putUserSetting(List<Map<String, String>> userSettings, Long id) {
        try {
            Optional<User> getOneUser = userRepository.findById(id);
            User user = new User();
            if(getOneUser.isPresent()) {
                user = getOneUser.get();
            }

            if(user.getIsActive().equals(true)) {
                user.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);

                List<UserSetting> uSetRes = userSettingRepository.findByIdUser(id);

                for (UserSetting update : uSetRes) {
                    UserSetting getOne = userSettingRepository.findById(update.getId()).get();
                    for (Map<String, String> map : userSettings) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if (Objects.equals(getOne.getKey(), entry.getKey())) {
                                getOne.setValue(entry.getValue());
                            }
                            userSettingRepository.save(getOne);
                        }
                    }
                }

                List<UserSetting> uSets = userSettingRepository.findByIdUser(id);
                Map<String, String> userSet = new HashMap<>();

                for (UserSetting uSet : uSets) {
                    userSet.put(uSet.getKey(), uSet.getValue());
                }

                List<Map.Entry<String, String>> listAll = new ArrayList<>(userSet.entrySet());

                return UserRs.builder()
                        .userData(UserDataRs.builder()
                                .id(user.getId())
                                .ssn(user.getSsn())
                                .firstName(user.getFirstName())
                                .lastName(user.getFamilyName())
                                .birthDate(user.getBirthDate().toString())
                                .createdTime(user.getCreatedTime().toString())
                                .updatedTime(user.getUpdateTime().toString())
                                .createdBy(user.getCreatedBy())
                                .updatedBy(user.getUpdatedBy())
                                .isActive(user.getIsActive())
                                .build())
                        .userSettings(listAll)
                        .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public User deleteUser(Long id) {
        try {
            Optional<User> userDb =userRepository.findById(id);
            User user = new User();
            if(userDb.isPresent()) {
                user = userDb.get();
            }
            if(user.getIsActive().equals(true)) {
                user.setIsActive(false);
                user.setDeletedTime(LocalDateTime.now());

                userRepository.save(user);

                return user;
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
    public UserRs refreshUser(Long id) {
        try {
            Optional<User> userDb =userRepository.findById(id);
            User user = new User();
            if(userDb.isPresent()) {
                user = userDb.get();
            }
            if(user.getIsActive().equals(false)) {
                user.setUpdateTime(LocalDateTime.now());
                user.setDeletedTime(null);
                user.setIsActive(true);
                userRepository.save(user);

                List<UserSetting> uSets = userSettingRepository.findByIdUser(id);

                Map<String, String> userSet = new HashMap<>();

                for (UserSetting uSet : uSets) {
                    userSet.put(uSet.getKey(), uSet.getValue());
                }

                List<Map.Entry<String, String>> listAll = new ArrayList<>(userSet.entrySet());


                return UserRs.builder()
                        .userData(UserDataRs.builder()
                                .id(user.getId())
                                .ssn(user.getSsn())
                                .firstName(user.getFirstName())
                                .lastName(user.getFamilyName())
                                .birthDate(user.getBirthDate().toString())
                                .createdTime(user.getCreatedTime().toString())
                                .updatedTime(user.getUpdateTime().toString())
                                .createdBy(user.getCreatedBy())
                                .updatedBy(user.getUpdatedBy())
                                .isActive(user.getIsActive())
                                .build())
                        .userSettings(listAll)
                        .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


}
