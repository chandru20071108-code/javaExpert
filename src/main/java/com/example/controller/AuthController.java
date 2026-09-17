
package com.example.controller;

import com.example.entity.Studententity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PersistenceContext
    private EntityManager entityManager;

    // Register
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody Studententity student) {

        String username = student.getUsername();

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Username is required");
        }

        if (student.getPassword() == null ||
                student.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Password is required");
        }

        try {
            entityManager.createQuery(
                    "SELECT s FROM Studententity s WHERE s.username = :username",
                    Studententity.class
            )
            .setParameter("username", username)
            .getSingleResult();

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");

        } catch (NoResultException e) {

            entityManager.persist(student);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration Successful");
        }
    }

    // Login
    @Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody Map<String, String> loginData) {

        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Username not found");
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Password");
        }

        Studententity student;

        try {
            student = entityManager.createQuery(
                    "SELECT s FROM Studententity s WHERE s.username = :username",
                    Studententity.class
            )
            .setParameter("username", username)
            .getSingleResult();

        } catch (NoResultException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Username not found");
        }

        if (!student.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Password");
        }

        return ResponseEntity.ok(
                "Login Successful for " + student.getName()
        );
    }
}