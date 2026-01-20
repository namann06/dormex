package com.dormex.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Size(max = 50)
    private String roomType;

    @Size(max = 255)
    private String amenities;
}
