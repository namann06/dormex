package com.dormex.dto.menu;

import com.dormex.entity.enums.DayOfWeek;
import com.dormex.entity.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMenuRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotBlank(message = "Menu items are required")
    @Size(max = 500, message = "Items cannot exceed 500 characters")
    private String items;

    @Size(max = 200, message = "Special note cannot exceed 200 characters")
    private String specialNote;
}
