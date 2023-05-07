package com.atc.be.code.test.controller;

import com.atc.be.code.test.domain.User;
import com.atc.be.code.test.model.request.UserRq;
import com.atc.be.code.test.model.response.UserRs;
import com.atc.be.code.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(@RequestParam(defaultValue = "5") int max_records, @RequestParam(defaultValue = "0") int offset) {
        try {
            return new ResponseEntity<>(userService.getAll(max_records, offset), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
        try {
            UserRs user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody UserRq request) {
        try {

            UserRs user = userService.createUser(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> UpdateUser(@RequestBody UserRq request, @PathVariable("id") Long id){
        try{
            UserRs user = userService.putUser(request, id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/users/{id}/settings")
    public ResponseEntity<Object> updateUserSettings(@PathVariable("id") Long id, @RequestBody List<Map<String,String>> data){
        try{
            UserRs user = userService.putUserSetting(data, id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        try{
            User user = userService.deleteUser( id);
            if( null == user ){
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/users/{id}/refresh")
    public ResponseEntity<Object> refreshUser(@PathVariable("id") Long id){
        try{
            UserRs user = userService.refreshUser(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
