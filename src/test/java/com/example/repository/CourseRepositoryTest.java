package com.example.repository;

import com.example.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldSaveAndFindCourse() {
        Course course = new Course();
        course.setCourseName("Java Programming");
        course.setDepartment("Computer Science");
        course.setDuration(6);
        course.setFees(25000.00);

        Course saved = courseRepository.save(course);

        assertThat(saved.getCourseId()).isNotNull();
        assertThat(courseRepository.findById(saved.getCourseId())).isPresent();
        assertThat(courseRepository.findById(saved.getCourseId()).get().getCourseName()).isEqualTo("Java Programming");
    }
}
