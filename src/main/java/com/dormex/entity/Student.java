package com.dormex.entity;

import com.dormex.entity.enums.StudentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_roll", columnList = "rollNumber", unique = true),
    @Index(name = "idx_student_user", columnList = "user_id"),
    @Index(name = "idx_student_room", columnList = "room_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String rollNumber;

    @Size(max = 15)
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    @Column(length = 15)
    private String phone;

    @Size(max = 100)
    @Column(length = 100)
    private String department;

    @Size(max = 10)
    @Column(length = 10)
    private String year;

    @Size(max = 500)
    @Column(length = 500)
    private String address;

    @Size(max = 100)
    @Column(length = 100)
    private String guardianName;

    @Size(max = 15)
    @Column(length = 15)
    private String guardianPhone;

    private LocalDate dateOfBirth;

    private LocalDate joiningDate;

    private LocalDate leavingDate;

    @Column(name = "room_id")
    private Long roomId; // Will be linked to Room entity in Step 7

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
