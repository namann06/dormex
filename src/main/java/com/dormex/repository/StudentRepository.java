package com.dormex.repository;

import com.dormex.entity.Student;
import com.dormex.entity.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByUserId(Long userId);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByUserId(Long userId);

    List<Student> findByStatus(StudentStatus status);

    List<Student> findByRoomId(Long roomId);

    @Query("SELECT s FROM Student s JOIN s.user u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchByNameOrRoll(@Param("keyword") String keyword);

    @Query("SELECT s FROM Student s WHERE s.roomId = :roomId AND s.status = 'ACTIVE'")
    List<Student> findActiveByRoomId(@Param("roomId") Long roomId);

    long countByStatus(StudentStatus status);

    long countByRoomIdIsNotNullAndStatus(StudentStatus status);
}
