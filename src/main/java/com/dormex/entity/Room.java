package com.dormex.entity;

import com.dormex.entity.enums.RoomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_block", columnList = "block_id"),
    @Index(name = "idx_room_number", columnList = "roomNumber")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"block_id", "roomNumber"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String roomNumber;

    @Min(1)
    @Column(nullable = false)
    private Integer floor;

    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Size(max = 50)
    @Column(length = 50)
    private String roomType; // Single, Double, Triple, Dormitory

    @Size(max = 255)
    @Column(length = 255)
    private String amenities;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean hasVacancy() {
        return currentOccupancy < capacity && status == RoomStatus.AVAILABLE;
    }

    public void updateStatus() {
        if (status == RoomStatus.UNDER_MAINTENANCE) return;
        if (currentOccupancy >= capacity) {
            status = RoomStatus.FULL;
        } else if (currentOccupancy > 0) {
            status = RoomStatus.OCCUPIED;
        } else {
            status = RoomStatus.AVAILABLE;
        }
    }
}
