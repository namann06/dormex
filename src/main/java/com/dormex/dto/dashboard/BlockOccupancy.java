package com.dormex.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockOccupancy {

    private Long blockId;
    private String blockName;
    private long totalRooms;
    private long totalCapacity;
    private long currentOccupancy;
    private double occupancyRate;
}
