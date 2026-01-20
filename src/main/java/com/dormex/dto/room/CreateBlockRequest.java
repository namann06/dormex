package com.dormex.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlockRequest {

    @NotBlank(message = "Block name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;

    private Integer totalFloors;
}
