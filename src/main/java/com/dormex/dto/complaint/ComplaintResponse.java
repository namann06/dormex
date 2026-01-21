package com.dormex.dto.complaint;

import com.dormex.entity.enums.ComplaintCategory;
import com.dormex.entity.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private ComplaintCategory category;
    private String title;
    private String description;
    private ComplaintStatus status;
    private String adminRemarks;
    private Long assignedTo;
    private String assignedToName;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
