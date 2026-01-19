package com.dormex.service;

import com.dormex.dto.student.CreateStudentRequest;
import com.dormex.dto.student.StudentResponse;
import com.dormex.dto.student.UpdateStudentRequest;
import com.dormex.entity.Student;
import com.dormex.entity.User;
import com.dormex.entity.enums.AuthProvider;
import com.dormex.entity.enums.Role;
import com.dormex.entity.enums.StudentStatus;
import com.dormex.exception.BadRequestException;
import com.dormex.exception.ResourceNotFoundException;
import com.dormex.repository.StudentRepository;
import com.dormex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new BadRequestException("Roll number already exists");
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.STUDENT)
            .authProvider(AuthProvider.LOCAL)
            .enabled(true)
            .build();
        user = userRepository.save(user);

        Student student = Student.builder()
            .user(user)
            .rollNumber(request.getRollNumber())
            .phone(request.getPhone())
            .department(request.getDepartment())
            .year(request.getYear())
            .address(request.getAddress())
            .guardianName(request.getGuardianName())
            .guardianPhone(request.getGuardianPhone())
            .dateOfBirth(request.getDateOfBirth())
            .joiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now())
            .status(StudentStatus.ACTIVE)
            .build();

        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = findStudentById(id);
        return mapToResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "rollNumber", rollNumber));
        return mapToResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(String keyword) {
        return studentRepository.searchByNameOrRoll(keyword).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = findStudentById(id);
        User user = student.getUser();

        if (request.getName() != null) {
            user.setName(request.getName());
            userRepository.save(user);
        }
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getDepartment() != null) student.setDepartment(request.getDepartment());
        if (request.getYear() != null) student.setYear(request.getYear());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getGuardianName() != null) student.setGuardianName(request.getGuardianName());
        if (request.getGuardianPhone() != null) student.setGuardianPhone(request.getGuardianPhone());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());

        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    @Transactional
    public StudentResponse updateStudentStatus(Long id, StudentStatus status) {
        Student student = findStudentById(id);
        student.setStatus(status);

        if (status == StudentStatus.LEFT || status == StudentStatus.TRANSFERRED) {
            student.setLeavingDate(LocalDate.now());
            student.setRoomId(null);
        }

        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    @Transactional
    public StudentResponse assignRoom(Long studentId, Long roomId) {
        Student student = findStudentById(studentId);

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new BadRequestException("Cannot assign room to non-active student");
        }

        student.setRoomId(roomId);
        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudentById(id);
        User user = student.getUser();

        studentRepository.delete(student);
        user.setEnabled(false);
        userRepository.save(user);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private StudentResponse mapToResponse(Student student) {
        User user = student.getUser();
        return StudentResponse.builder()
            .id(student.getId())
            .userId(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .rollNumber(student.getRollNumber())
            .phone(student.getPhone())
            .department(student.getDepartment())
            .year(student.getYear())
            .address(student.getAddress())
            .guardianName(student.getGuardianName())
            .guardianPhone(student.getGuardianPhone())
            .dateOfBirth(student.getDateOfBirth())
            .joiningDate(student.getJoiningDate())
            .leavingDate(student.getLeavingDate())
            .roomId(student.getRoomId())
            .status(student.getStatus())
            .profilePicture(user.getProfilePicture())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }
}
