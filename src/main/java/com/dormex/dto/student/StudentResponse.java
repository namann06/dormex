package com.dormex.dto.student;

import com.dormex.entity.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String rollNumber;
    private String phone;
    private String department;
    private String year;
    private String address;
    private String guardianName;
    private String guardianPhone;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private LocalDate leavingDate;
    private Long roomId;
    private String roomNumber;
    private StudentStatus status;
    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
