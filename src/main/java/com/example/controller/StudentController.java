package com.example.controller;

import com.example.entity.Studententity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public List<Studententity> getAllStudents() {
        return entityManager.createQuery(
                "SELECT s FROM Studententity s",
                Studententity.class
        ).getResultList();
    }

    @GetMapping("/{id}")
    public Studententity getStudentById(@PathVariable int id) {
        return entityManager.find(Studententity.class, id);
    }

    @Transactional
    @PostMapping
    public Studententity addStudent(@RequestBody Studententity student) {
        entityManager.persist(student);
        return student;
    }

    @Transactional
    @PutMapping("/{id}")
    public Studententity updateStudent(
            @PathVariable int id,
            @RequestBody Studententity student) {

        student.setId(id);
        return entityManager.merge(student);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {
        Studententity student =
                entityManager.find(Studententity.class, id);

        if (student != null) {
            entityManager.remove(student);
        }

        return "Student deleted successfully";
    }
}