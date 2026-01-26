package com.dormex.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {

    private long totalStudents;
    private long activeStudents;
    private long totalBlocks;
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long totalComplaints;
    private long openComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private double occupancyRate;
}
