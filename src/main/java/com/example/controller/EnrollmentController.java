package com.example.controller;

import com.example.entity.Course;
import com.example.entity.Enrollment;
import com.example.entity.Studententity;
import com.example.repository.CourseRepository;
import com.example.repository.EnrollmentRepository;
import com.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                               StudentRepository studentRepository,
                               CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnrollmentById(@PathVariable Integer id) {
        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);
        if (enrollment.isPresent()) {
            return ResponseEntity.ok(enrollment.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Enrollment not found");
    }

    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody Enrollment enrollment) {
        if (enrollment == null) {
            return ResponseEntity.badRequest().body("Invalid enrollment data");
        }

        if (enrollment.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        if (enrollment.getCourseId() == null) {
            return ResponseEntity.badRequest().body("Course not found");
        }

        Optional<Studententity> student = studentRepository.findById(enrollment.getStudentId());
        if (student.isEmpty()) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        Optional<Course> course = courseRepository.findById(enrollment.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.badRequest().body("Course not found");
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                enrollment.getStudentId(), enrollment.getCourseId());
        if (alreadyEnrolled) {
            return ResponseEntity.badRequest().body("Student already enrolled");
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(savedEnrollment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnrollment(@PathVariable Integer id, @RequestBody Enrollment enrollment) {
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findById(id);
        if (existingEnrollment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found");
        }

        Enrollment update = existingEnrollment.get();
        update.setStudentId(enrollment.getStudentId());
        update.setCourseId(enrollment.getCourseId());
        update.setEnrollmentDate(enrollment.getEnrollmentDate());
        update.setStatus(enrollment.getStatus());

        return ResponseEntity.ok(enrollmentRepository.save(update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEnrollment(@PathVariable Integer id) {
        if (!enrollmentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found");
        }

        enrollmentRepository.deleteById(id);
        return ResponseEntity.ok("Enrollment deleted successfully");
    }
}
