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
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174"})
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

    private Integer parseInteger(Object value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(trimmed);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(fieldName + " must be a valid integer");
            }
        }

        throw new IllegalArgumentException(fieldName + " must be a valid integer");
    }

    private Double parseDouble(Object value, String fieldName) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(trimmed);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(fieldName + " must be a valid number");
            }
        }

        throw new IllegalArgumentException(fieldName + " must be a valid number");
    }

    private Marks buildMarksFromMap(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }

        Marks marks = new Marks();
        marks.setStudentId(parseInteger(payload.get("studentId"), "studentId"));
        marks.setCourseId(parseInteger(payload.get("courseId"), "courseId"));

        Object examName = payload.get("examName");
        marks.setExamName(examName == null ? null : examName.toString());

        Object marksValue = payload.get("marks");
        if (marksValue == null) {
            marksValue = payload.get("mark");
        }
        if (marksValue == null) {
            marksValue = payload.get("score");
        }
        marks.setMarks(parseDouble(marksValue, "marks"));

        Object totalMarks = payload.get("totalMarks");
        if (totalMarks == null) {
            totalMarks = payload.get("total");
        }
        marks.setTotalMarks(parseDouble(totalMarks, "totalMarks"));

        return marks;
    }

    @PostMapping
    public ResponseEntity<?> createMarks(@RequestBody Map<String, Object> payload) {
        try {
            Marks marks = buildMarksFromMap(payload);
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

            if (marks.getExamName() == null || marks.getExamName().isBlank()) {
                return ResponseEntity.badRequest().body("Exam name is required");
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
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarks(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Optional<Marks> existingMarks = marksRepository.findById(id);
            if (existingMarks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Marks not found");
            }

            Marks marks = buildMarksFromMap(payload);
            if (marks == null) {
                return ResponseEntity.badRequest().body("Invalid marks data");
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
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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
