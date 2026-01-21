package com.dormex.repository;

import com.dormex.entity.Complaint;
import com.dormex.entity.enums.ComplaintCategory;
import com.dormex.entity.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCategory(ComplaintCategory category);

    List<Complaint> findByStatusAndCategory(ComplaintStatus status, ComplaintCategory category);

    List<Complaint> findByAssignedTo(Long adminId);

    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    List<Complaint> findAllOrderByCreatedAtDesc();

    @Query("SELECT c FROM Complaint c WHERE c.status IN ('OPEN', 'IN_PROGRESS') ORDER BY c.createdAt ASC")
    List<Complaint> findPendingComplaints();

    long countByStatus(ComplaintStatus status);

    long countByCategory(ComplaintCategory category);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.createdAt >= :since")
    long countComplaintsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'RESOLVED' AND c.resolvedAt >= :since")
    long countResolvedSince(@Param("since") LocalDateTime since);
}
