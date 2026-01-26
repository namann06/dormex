package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.dashboard.*;
import com.dormex.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Dashboard", description = "Admin dashboard APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get overall statistics")
    public ResponseEntity<ApiResponse<DashboardStats>> getStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", dashboardService.getStats()));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent activity")
    public ResponseEntity<ApiResponse<RecentActivity>> getRecentActivity(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved", dashboardService.getRecentActivity(limit)));
    }

    @GetMapping("/block-occupancy")
    @Operation(summary = "Get block-wise occupancy")
    public ResponseEntity<ApiResponse<List<BlockOccupancy>>> getBlockOccupancy() {
        return ResponseEntity.ok(ApiResponse.success("Block occupancy retrieved", dashboardService.getBlockOccupancy()));
    }

    @GetMapping("/complaint-summary")
    @Operation(summary = "Get complaint summary")
    public ResponseEntity<ApiResponse<ComplaintSummary>> getComplaintSummary() {
        return ResponseEntity.ok(ApiResponse.success("Complaint summary retrieved", dashboardService.getComplaintSummary()));
    }
}
