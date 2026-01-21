package com.dormex.dto.complaint;

import com.dormex.entity.enums.ComplaintStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintRequest {

    private ComplaintStatus status;

    @Size(max = 1000)
    private String adminRemarks;

    private Long assignedTo;
}
