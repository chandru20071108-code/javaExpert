package com.example.controller;

import com.example.entity.Course;
import com.example.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173", "http://127.0.0.1:5174"})
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @Transactional
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setDepartment(course.getDepartment());
        existingCourse.setDuration(course.getDuration());
        existingCourse.setFees(course.getFees());

        return courseRepository.save(existingCourse);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        courseRepository.delete(course);
        return "Course deleted successfully";
    }
}
