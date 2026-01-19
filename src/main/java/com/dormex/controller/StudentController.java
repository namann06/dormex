package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.student.CreateStudentRequest;
import com.dormex.dto.student.StudentResponse;
import com.dormex.dto.student.UpdateStudentRequest;
import com.dormex.entity.enums.StudentStatus;
import com.dormex.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "CRUD operations for student records")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new student (Admin only)")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.ok(ApiResponse.success("Student created", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all students (Admin only)")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/roll/{rollNumber}")
    @Operation(summary = "Get student by roll number")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByRoll(@PathVariable String rollNumber) {
        StudentResponse response = studentService.getStudentByRollNumber(rollNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search students by name or roll number")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam String keyword) {
        List<StudentResponse> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get students by status")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByStatus(
            @PathVariable StudentStatus status) {
        List<StudentResponse> students = studentService.getStudentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student details (Admin only)")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student status (Admin only)")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentStatus(
            @PathVariable Long id,
            @RequestParam StudentStatus status) {
        StudentResponse response = studentService.updateStudentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @PatchMapping("/{id}/room")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign room to student (Admin only)")
    public ResponseEntity<ApiResponse<StudentResponse>> assignRoom(
            @PathVariable Long id,
            @RequestParam Long roomId) {
        StudentResponse response = studentService.assignRoom(id, roomId);
        return ResponseEntity.ok(ApiResponse.success("Room assigned", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted", null));
    }
}
