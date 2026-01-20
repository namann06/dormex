package com.dormex.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponse {

    private Long id;
    private String name;
    private String description;
    private Integer totalFloors;
    private boolean active;
    private Integer totalRooms;
    private Integer occupiedRooms;
    private LocalDateTime createdAt;
}
