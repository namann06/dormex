package com.dormex.service;

import com.dormex.dto.complaint.ComplaintResponse;
import com.dormex.dto.dashboard.*;
import com.dormex.dto.student.StudentResponse;
import com.dormex.entity.Block;
import com.dormex.entity.Complaint;
import com.dormex.entity.Room;
import com.dormex.entity.Student;
import com.dormex.entity.enums.ComplaintStatus;
import com.dormex.entity.enums.RoomStatus;
import com.dormex.entity.enums.StudentStatus;
import com.dormex.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final BlockRepository blockRepository;
    private final RoomRepository roomRepository;
    private final ComplaintRepository complaintRepository;

    public DashboardStats getStats() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long totalBlocks = blockRepository.countByActiveTrue();
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED) + 
                            roomRepository.countByStatus(RoomStatus.FULL);
        
        long totalComplaints = complaintRepository.count();
        long openComplaints = complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long inProgressComplaints = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.RESOLVED) +
                                  complaintRepository.countByStatus(ComplaintStatus.CLOSED);

        // Calculate occupancy rate
        long totalCapacity = roomRepository.findAll().stream()
                .mapToInt(Room::getCapacity)
                .sum();
        long currentOccupancy = roomRepository.findAll().stream()
                .mapToInt(Room::getCurrentOccupancy)
                .sum();
        double occupancyRate = totalCapacity > 0 ? (double) currentOccupancy / totalCapacity * 100 : 0;

        return DashboardStats.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .totalBlocks(totalBlocks)
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .totalComplaints(totalComplaints)
                .openComplaints(openComplaints)
                .inProgressComplaints(inProgressComplaints)
                .resolvedComplaints(resolvedComplaints)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .build();
    }

    public RecentActivity getRecentActivity(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<ComplaintResponse> recentComplaints = complaintRepository.findAll(pageRequest)
                .getContent().stream()
                .map(this::mapComplaintToResponse)
                .collect(Collectors.toList());

        List<StudentResponse> recentStudents = studentRepository.findAll(pageRequest)
                .getContent().stream()
                .map(this::mapStudentToResponse)
                .collect(Collectors.toList());

        return RecentActivity.builder()
                .recentComplaints(recentComplaints)
                .recentStudents(recentStudents)
                .build();
    }

    private ComplaintResponse mapComplaintToResponse(Complaint complaint) {
        Student student = complaint.getStudent();
        return ComplaintResponse.builder()
                .id(complaint.getId())
                .studentId(student.getId())
                .studentName(student.getUser().getName())
                .studentRollNumber(student.getRollNumber())
                .category(complaint.getCategory())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .status(complaint.getStatus())
                .adminRemarks(complaint.getAdminRemarks())
                .assignedTo(complaint.getAssignedTo())
                .resolvedAt(complaint.getResolvedAt())
                .createdAt(complaint.getCreatedAt())
                .updatedAt(complaint.getUpdatedAt())
                .build();
    }

    private StudentResponse mapStudentToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .rollNumber(student.getRollNumber())
                .phone(student.getPhone())
                .department(student.getDepartment())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .build();
    }

    public List<BlockOccupancy> getBlockOccupancy() {
        return blockRepository.findByActiveTrue().stream()
                .map(this::calculateBlockOccupancy)
                .collect(Collectors.toList());
    }

    private BlockOccupancy calculateBlockOccupancy(Block block) {
        List<Room> rooms = roomRepository.findByBlockId(block.getId());
        
        long totalRooms = rooms.size();
        long totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        long currentOccupancy = rooms.stream().mapToInt(Room::getCurrentOccupancy).sum();
        double occupancyRate = totalCapacity > 0 ? (double) currentOccupancy / totalCapacity * 100 : 0;

        return BlockOccupancy.builder()
                .blockId(block.getId())
                .blockName(block.getName())
                .totalRooms(totalRooms)
                .totalCapacity(totalCapacity)
                .currentOccupancy(currentOccupancy)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .build();
    }

    public ComplaintSummary getComplaintSummary() {
        Map<String, Long> byStatus = new HashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            byStatus.put(status.name(), complaintRepository.countByStatus(status));
        }

        Map<String, Long> byCategory = new HashMap<>();
        complaintRepository.findAll().stream()
                .collect(Collectors.groupingBy(c -> c.getCategory().name(), Collectors.counting()))
                .forEach(byCategory::put);

        return ComplaintSummary.builder()
                .total(complaintRepository.count())
                .byStatus(byStatus)
                .byCategory(byCategory)
                .build();
    }
}
