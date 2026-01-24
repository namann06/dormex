package com.dormex.dto.menu;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMenuRequest {

    @Size(max = 500, message = "Items cannot exceed 500 characters")
    private String items;

    @Size(max = 200, message = "Special note cannot exceed 200 characters")
    private String specialNote;
}
