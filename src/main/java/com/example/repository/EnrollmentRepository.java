package com.example.repository;

import com.example.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
