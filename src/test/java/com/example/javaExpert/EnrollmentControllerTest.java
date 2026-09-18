package com.example.javaExpert;

import com.example.controller.EnrollmentController;
import com.example.entity.Course;
import com.example.entity.Enrollment;
import com.example.entity.EnrollmentStatus;
import com.example.entity.Studententity;
import com.example.repository.CourseRepository;
import com.example.repository.EnrollmentRepository;
import com.example.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnrollmentControllerTest {

    @Autowired
    private EnrollmentController enrollmentController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldCreateValidEnrollment() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentDate(LocalDate.of(2026, 9, 17));
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        ResponseEntity<?> response = enrollmentController.createEnrollment(enrollment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Enrollment.class, response.getBody());
        Enrollment saved = (Enrollment) response.getBody();
        assertEquals(student.getId(), saved.getStudentId());
        assertEquals(course.getCourseId(), saved.getCourseId());
        assertEquals(EnrollmentStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void shouldRejectEnrollmentWhenStudentDoesNotExist() {
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(999);
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentDate(LocalDate.of(2026, 9, 17));
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        ResponseEntity<?> response = enrollmentController.createEnrollment(enrollment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Student not found", response.getBody());
    }

    @Test
    void shouldRejectEnrollmentWhenCourseDoesNotExist() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(999);
        enrollment.setEnrollmentDate(LocalDate.of(2026, 9, 17));
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        ResponseEntity<?> response = enrollmentController.createEnrollment(enrollment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Course not found", response.getBody());
    }

    @Test
    void shouldRejectDuplicateEnrollment() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentDate(LocalDate.of(2026, 9, 17));
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        enrollmentController.createEnrollment(enrollment);
        ResponseEntity<?> duplicateResponse = enrollmentController.createEnrollment(enrollment);

        assertEquals(HttpStatus.BAD_REQUEST, duplicateResponse.getStatusCode());
        assertEquals("Student already enrolled", duplicateResponse.getBody());
    }

    @Test
    void shouldGetAllEnrollmentsAndUpdateStatusAndDelete() {
        Studententity student = studentRepository.save(new Studententity(null, "Alice", "CS", 20, "alice", "pass123"));
        Course course = courseRepository.save(new Course(null, "Java", "CS", 6, 2000.0));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentDate(LocalDate.of(2026, 9, 17));
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        ResponseEntity<?> created = enrollmentController.createEnrollment(enrollment);
        Enrollment saved = (Enrollment) created.getBody();

        ResponseEntity<List<Enrollment>> allResponse = enrollmentController.getAllEnrollments();
        assertEquals(HttpStatus.OK, allResponse.getStatusCode());
        assertEquals(1, allResponse.getBody().size());

        ResponseEntity<?> getByIdResponse = enrollmentController.getEnrollmentById(saved.getEnrollmentId());
        assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());

        Enrollment update = new Enrollment();
        update.setStudentId(saved.getStudentId());
        update.setCourseId(saved.getCourseId());
        update.setEnrollmentDate(saved.getEnrollmentDate());
        update.setStatus(EnrollmentStatus.COMPLETED);

        ResponseEntity<?> updateResponse = enrollmentController.updateEnrollment(saved.getEnrollmentId(), update);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Enrollment updated = (Enrollment) updateResponse.getBody();
        assertEquals(EnrollmentStatus.COMPLETED, updated.getStatus());

        ResponseEntity<?> deleteResponse = enrollmentController.deleteEnrollment(saved.getEnrollmentId());
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals("Enrollment deleted successfully", deleteResponse.getBody());
    }
}
