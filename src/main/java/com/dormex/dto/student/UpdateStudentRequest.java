package com.dormex.dto.student;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequest {

    @Size(min = 2, max = 100)
    private String name;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Size(max = 100)
    private String department;

    @Size(max = 10)
    private String year;

    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String guardianName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid guardian phone")
    private String guardianPhone;

    private LocalDate dateOfBirth;
}
