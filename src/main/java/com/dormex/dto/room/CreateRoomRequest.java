package com.dormex.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    @NotNull(message = "Block ID is required")
    private Long blockId;

    @NotBlank(message = "Room number is required")
    @Size(max = 20)
    private String roomNumber;

    @NotNull(message = "Floor is required")
    @Min(value = 1, message = "Floor must be at least 1")
    private Integer floor;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity = 1;

    @Size(max = 50)
    private String roomType;

    @Size(max = 255)
    private String amenities;
}
