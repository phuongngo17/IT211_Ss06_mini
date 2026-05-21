package org.example.btth.repository;

import org.example.btth.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho entity Course.
 * Kế thừa JpaRepository để có sẵn các thao tác CRUD và phân trang.
 * Có thể bổ sung các phương thức tìm kiếm tùy chỉnh tại đây nếu cần.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
