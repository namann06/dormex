package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.complaint.ComplaintResponse;
import com.dormex.dto.complaint.CreateComplaintRequest;
import com.dormex.dto.complaint.UpdateComplaintRequest;
import com.dormex.entity.enums.ComplaintCategory;
import com.dormex.entity.enums.ComplaintStatus;
import com.dormex.security.CustomUserDetails;
import com.dormex.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint Management", description = "Raise and manage complaints")
@SecurityRequirement(name = "Bearer Authentication")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Raise a new complaint (Student only)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateComplaintRequest request) {
        ComplaintResponse response = complaintService.createComplaint(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Complaint raised", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my complaints (Student only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ComplaintResponse> complaints = complaintService.getMyComplaints(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all complaints (Admin only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints() {
        List<ComplaintResponse> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by ID")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(@PathVariable Long id) {
        ComplaintResponse response = complaintService.getComplaintById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complaints by student (Admin only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getComplaintsByStudent(
            @PathVariable Long studentId) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complaints by status (Admin only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getComplaintsByStatus(
            @PathVariable ComplaintStatus status) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complaints by category (Admin only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getComplaintsByCategory(
            @PathVariable ComplaintCategory category) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending complaints (Admin only)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getPendingComplaints() {
        List<ComplaintResponse> complaints = complaintService.getPendingComplaints();
        return ResponseEntity.ok(ApiResponse.success(complaints));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update complaint (Admin only)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintRequest request) {
        ComplaintResponse response = complaintService.updateComplaint(id, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint updated", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update complaint status (Admin only)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status,
            @RequestParam(required = false) String remarks) {
        ComplaintResponse response = complaintService.updateStatus(id, status, remarks);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete complaint (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted", null));
    }
}
