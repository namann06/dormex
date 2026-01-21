package com.dormex.service;

import com.dormex.dto.complaint.ComplaintResponse;
import com.dormex.dto.complaint.CreateComplaintRequest;
import com.dormex.dto.complaint.UpdateComplaintRequest;
import com.dormex.entity.Complaint;
import com.dormex.entity.Student;
import com.dormex.entity.User;
import com.dormex.entity.enums.ComplaintCategory;
import com.dormex.entity.enums.ComplaintStatus;
import com.dormex.exception.BadRequestException;
import com.dormex.exception.ResourceNotFoundException;
import com.dormex.repository.ComplaintRepository;
import com.dormex.repository.StudentRepository;
import com.dormex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ComplaintResponse createComplaint(Long userId, CreateComplaintRequest request) {
        Student student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new BadRequestException("Student profile not found"));

        Complaint complaint = Complaint.builder()
            .student(student)
            .category(request.getCategory())
            .title(request.getTitle())
            .description(request.getDescription())
            .status(ComplaintStatus.OPEN)
            .build();

        complaint = complaintRepository.save(complaint);
        return mapToResponse(complaint);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id) {
        Complaint complaint = findComplaintById(id);
        return mapToResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAllOrderByCreatedAtDesc().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(Long userId) {
        Student student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new BadRequestException("Student profile not found"));

        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByCategory(ComplaintCategory category) {
        return complaintRepository.findByCategory(category).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getPendingComplaints() {
        return complaintRepository.findPendingComplaints().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public ComplaintResponse updateComplaint(Long id, UpdateComplaintRequest request) {
        Complaint complaint = findComplaintById(id);

        if (request.getStatus() != null) {
            complaint.setStatus(request.getStatus());
            if (request.getStatus() == ComplaintStatus.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getAdminRemarks() != null) {
            complaint.setAdminRemarks(request.getAdminRemarks());
        }
        if (request.getAssignedTo() != null) {
            complaint.setAssignedTo(request.getAssignedTo());
        }

        complaint = complaintRepository.save(complaint);
        return mapToResponse(complaint);
    }

    @Transactional
    public ComplaintResponse updateStatus(Long id, ComplaintStatus status, String remarks) {
        Complaint complaint = findComplaintById(id);
        complaint.setStatus(status);

        if (remarks != null) {
            complaint.setAdminRemarks(remarks);
        }
        if (status == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        complaint = complaintRepository.save(complaint);
        return mapToResponse(complaint);
    }

    @Transactional
    public void deleteComplaint(Long id) {
        Complaint complaint = findComplaintById(id);
        complaintRepository.delete(complaint);
    }

    private Complaint findComplaintById(Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));
    }

    private ComplaintResponse mapToResponse(Complaint complaint) {
        Student student = complaint.getStudent();
        User studentUser = student.getUser();

        String assignedToName = null;
        if (complaint.getAssignedTo() != null) {
            assignedToName = userRepository.findById(complaint.getAssignedTo())
                .map(User::getName)
                .orElse(null);
        }

        return ComplaintResponse.builder()
            .id(complaint.getId())
            .studentId(student.getId())
            .studentName(studentUser.getName())
            .studentRollNumber(student.getRollNumber())
            .category(complaint.getCategory())
            .title(complaint.getTitle())
            .description(complaint.getDescription())
            .status(complaint.getStatus())
            .adminRemarks(complaint.getAdminRemarks())
            .assignedTo(complaint.getAssignedTo())
            .assignedToName(assignedToName)
            .resolvedAt(complaint.getResolvedAt())
            .createdAt(complaint.getCreatedAt())
            .updatedAt(complaint.getUpdatedAt())
            .build();
    }
}
