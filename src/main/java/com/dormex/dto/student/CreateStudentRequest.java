package com.dormex.dto.student;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20)
    private String rollNumber;

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

    private LocalDate joiningDate;
}
