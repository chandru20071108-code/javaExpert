package com.example.javaExpert;

import com.example.controller.MarksController;
import com.example.entity.Course;
import com.example.entity.Marks;
import com.example.entity.Studententity;
import com.example.repository.CourseRepository;
import com.example.repository.MarksRepository;
import com.example.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MarksControllerTest {

    @Autowired
    private MarksController marksController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MarksRepository marksRepository;

    @BeforeEach
    void setUp() {
        marksRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldCreateAndValidateMarks() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Marks validMarks = new Marks();
        validMarks.setStudentId(student.getId());
        validMarks.setCourseId(course.getCourseId());
        validMarks.setExamName("Internal 1");
        validMarks.setMarks(78.0);
        validMarks.setTotalMarks(100.0);

        ResponseEntity<?> created = marksController.createMarks(validMarks);
        assertEquals(HttpStatus.OK, created.getStatusCode());
        assertInstanceOf(Marks.class, created.getBody());

        ResponseEntity<?> negative = marksController.createMarks(new Marks(null, student.getId(), course.getCourseId(), "Internal 2", -10.0, 100.0));
        assertEquals(HttpStatus.BAD_REQUEST, negative.getStatusCode());
        assertEquals("Marks cannot be negative", negative.getBody());

        ResponseEntity<?> tooHigh = marksController.createMarks(new Marks(null, student.getId(), course.getCourseId(), "Internal 3", 105.0, 100.0));
        assertEquals(HttpStatus.BAD_REQUEST, tooHigh.getStatusCode());
        assertEquals("Marks cannot be greater than total marks", tooHigh.getBody());

        ResponseEntity<?> invalidStudent = marksController.createMarks(new Marks(null, 999, course.getCourseId(), "Internal 4", 50.0, 100.0));
        assertEquals(HttpStatus.BAD_REQUEST, invalidStudent.getStatusCode());
        assertEquals("Student not found", invalidStudent.getBody());

        ResponseEntity<?> invalidCourse = marksController.createMarks(new Marks(null, student.getId(), 999, "Internal 5", 50.0, 100.0));
        assertEquals(HttpStatus.BAD_REQUEST, invalidCourse.getStatusCode());
        assertEquals("Course not found", invalidCourse.getBody());
    }

    @Test
    void shouldGetAllUpdateAndDeleteMarks() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Marks marks = new Marks();
        marks.setStudentId(student.getId());
        marks.setCourseId(course.getCourseId());
        marks.setExamName("Internal 1");
        marks.setMarks(78.0);
        marks.setTotalMarks(100.0);

        Marks saved = (Marks) marksController.createMarks(marks).getBody();

        ResponseEntity<List<Marks>> all = marksController.getAllMarks();
        assertEquals(HttpStatus.OK, all.getStatusCode());
        assertEquals(1, all.getBody().size());

        ResponseEntity<?> byId = marksController.getMarksById(saved.getMarkId());
        assertEquals(HttpStatus.OK, byId.getStatusCode());

        Marks updated = new Marks();
        updated.setStudentId(saved.getStudentId());
        updated.setCourseId(saved.getCourseId());
        updated.setExamName("Internal 2");
        updated.setMarks(90.0);
        updated.setTotalMarks(100.0);

        ResponseEntity<?> update = marksController.updateMarks(saved.getMarkId(), updated);
        assertEquals(HttpStatus.OK, update.getStatusCode());
        Marks updatedMarks = (Marks) update.getBody();
        assertEquals("Internal 2", updatedMarks.getExamName());
        assertEquals(90.0, updatedMarks.getMarks());

        ResponseEntity<?> delete = marksController.deleteMarks(saved.getMarkId());
        assertEquals(HttpStatus.OK, delete.getStatusCode());
        assertEquals("Marks deleted successfully", delete.getBody());
    }
}
