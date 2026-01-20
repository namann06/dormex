package com.dormex.dto.room;

import com.dormex.entity.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private Long blockId;
    private String blockName;
    private String roomNumber;
    private Integer floor;
    private Integer capacity;
    private Integer currentOccupancy;
    private Integer availableSlots;
    private RoomStatus status;
    private String roomType;
    private String amenities;
    private LocalDateTime createdAt;
}
