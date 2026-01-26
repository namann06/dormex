package com.dormex.dto.dashboard;

import com.dormex.dto.complaint.ComplaintResponse;
import com.dormex.dto.student.StudentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecentActivity {

    private List<ComplaintResponse> recentComplaints;
    private List<StudentResponse> recentStudents;
}
