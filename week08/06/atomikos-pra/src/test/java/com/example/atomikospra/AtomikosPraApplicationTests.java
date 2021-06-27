package com.example.atomikospra;

import com.example.atomikospra.dao.User;
import com.example.atomikospra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AtomikosPraApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void insertUser(){
        User user = new User();
        user.setId("1234569");
        user.setUserName("mike-666666666");
        try {
            userService.insertUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
