package com.example.controller;

import com.example.entity.Course;
import com.example.entity.Marks;
import com.example.entity.Studententity;
import com.example.repository.CourseRepository;
import com.example.repository.MarksRepository;
import com.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/marks")
public class MarksController {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public MarksController(MarksRepository marksRepository,
                          StudentRepository studentRepository,
                          CourseRepository courseRepository) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Marks>> getAllMarks() {
        return ResponseEntity.ok(marksRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMarksById(@PathVariable Integer id) {
        Optional<Marks> marks = marksRepository.findById(id);
        if (marks.isPresent()) {
            return ResponseEntity.ok(marks.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Marks not found");
    }

    @PostMapping
    public ResponseEntity<?> createMarks(@RequestBody Marks marks) {
        if (marks == null) {
            return ResponseEntity.badRequest().body("Invalid marks data");
        }

        if (marks.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        if (marks.getCourseId() == null) {
            return ResponseEntity.badRequest().body("Course not found");
        }

        Optional<Studententity> student = studentRepository.findById(marks.getStudentId());
        if (student.isEmpty()) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        Optional<Course> course = courseRepository.findById(marks.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.badRequest().body("Course not found");
        }

        if (marks.getMarks() == null || marks.getMarks() < 0) {
            return ResponseEntity.badRequest().body("Marks cannot be negative");
        }

        if (marks.getTotalMarks() == null || marks.getTotalMarks() <= 0) {
            return ResponseEntity.badRequest().body("Total marks must be greater than zero");
        }

        if (marks.getMarks() > marks.getTotalMarks()) {
            return ResponseEntity.badRequest().body("Marks cannot be greater than total marks");
        }

        return ResponseEntity.ok(marksRepository.save(marks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarks(@PathVariable Integer id, @RequestBody Marks marks) {
        Optional<Marks> existingMarks = marksRepository.findById(id);
        if (existingMarks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Marks not found");
        }

        if (marks.getStudentId() != null) {
            if (studentRepository.findById(marks.getStudentId()).isEmpty()) {
                return ResponseEntity.badRequest().body("Student not found");
            }
        }

        if (marks.getCourseId() != null) {
            if (courseRepository.findById(marks.getCourseId()).isEmpty()) {
                return ResponseEntity.badRequest().body("Course not found");
            }
        }

        if (marks.getMarks() != null && marks.getMarks() < 0) {
            return ResponseEntity.badRequest().body("Marks cannot be negative");
        }

        if (marks.getTotalMarks() != null && marks.getTotalMarks() <= 0) {
            return ResponseEntity.badRequest().body("Total marks must be greater than zero");
        }

        if (marks.getMarks() != null && marks.getTotalMarks() != null && marks.getMarks() > marks.getTotalMarks()) {
            return ResponseEntity.badRequest().body("Marks cannot be greater than total marks");
        }

        Marks update = existingMarks.get();
        update.setStudentId(marks.getStudentId() != null ? marks.getStudentId() : update.getStudentId());
        update.setCourseId(marks.getCourseId() != null ? marks.getCourseId() : update.getCourseId());
        update.setExamName(marks.getExamName() != null ? marks.getExamName() : update.getExamName());
        update.setMarks(marks.getMarks() != null ? marks.getMarks() : update.getMarks());
        update.setTotalMarks(marks.getTotalMarks() != null ? marks.getTotalMarks() : update.getTotalMarks());

        return ResponseEntity.ok(marksRepository.save(update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarks(@PathVariable Integer id) {
        if (!marksRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Marks not found");
        }

        marksRepository.deleteById(id);
        return ResponseEntity.ok("Marks deleted successfully");
    }
}
