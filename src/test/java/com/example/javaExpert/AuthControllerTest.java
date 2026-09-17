package com.example.javaExpert;

import com.example.controller.AuthController;
import com.example.entity.Studententity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    void registerAndLogin_shouldWorkForNewUser() {
        Studententity student = new Studententity();
        student.setName("Alice");
        student.setDepartment("Computer Science");
        student.setAge(20);
        student.setUsername("alice");
        student.setPassword("pass123");

        var registerResponse = authController.register(student);
        assertEquals("Registration Successful", registerResponse.getBody());
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        var duplicateRegisterResponse = authController.register(student);
        assertEquals("Username already exists", duplicateRegisterResponse.getBody());
        assertEquals(HttpStatus.CONFLICT, duplicateRegisterResponse.getStatusCode());

        var loginResponse = authController.login(Map.of("username", "alice", "password", "pass123"));
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertEquals("Login Successful for Alice", loginResponse.getBody());
    }
}
