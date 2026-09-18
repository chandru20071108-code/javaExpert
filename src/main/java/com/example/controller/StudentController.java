package com.example.controller;

import com.example.entity.Studententity;
import com.example.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174"
})
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private void applyDefaults(Studententity student) {
        if (student.getUsername() == null || student.getUsername().isBlank()) {
            student.setUsername("student_" + System.currentTimeMillis());
        }

        if (student.getPassword() == null || student.getPassword().isBlank()) {
            student.setPassword("default123");
        }
    }

    @GetMapping
    public List<Studententity> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Studententity> getStudentById(@PathVariable Integer id) {
        return studentRepository.findById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Studententity> addStudent(
            @RequestBody Studententity student) {

        applyDefaults(student);
        Studententity savedStudent = studentRepository.save(student);

        return new ResponseEntity<>(
                savedStudent,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Studententity> updateStudent(
            @PathVariable Integer id,
            @RequestBody Studententity student) {

        if (!studentRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        applyDefaults(student);
        student.setId(id);
        Studententity updatedStudent = studentRepository.save(student);

        return new ResponseEntity<>(
                updatedStudent,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {

        if (!studentRepository.existsById(id)) {
            return new ResponseEntity<>(
                    "Student not found",
                    HttpStatus.NOT_FOUND
            );
        }

        studentRepository.deleteById(id);

        return new ResponseEntity<>(
                "Student deleted successfully",
                HttpStatus.OK
        );
    }
}