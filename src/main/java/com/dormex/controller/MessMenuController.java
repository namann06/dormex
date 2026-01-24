package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.menu.*;
import com.dormex.entity.enums.DayOfWeek;
import com.dormex.entity.enums.MealType;
import com.dormex.service.MessMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Mess Menu", description = "Mess menu management APIs")
public class MessMenuController {

    private final MessMenuService menuService;

    // ========== Public/Student Endpoints ==========

    @GetMapping("/today")
    @Operation(summary = "Get today's menu")
    public ResponseEntity<ApiResponse<DailyMenuResponse>> getTodayMenu() {
        return ResponseEntity.ok(ApiResponse.success("Today's menu retrieved", menuService.getTodayMenu()));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get weekly menu")
    public ResponseEntity<ApiResponse<List<DailyMenuResponse>>> getWeeklyMenu() {
        return ResponseEntity.ok(ApiResponse.success("Weekly menu retrieved", menuService.getWeeklyMenu()));
    }

    @GetMapping("/day/{day}")
    @Operation(summary = "Get menu by day")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenuByDay(@PathVariable DayOfWeek day) {
        return ResponseEntity.ok(ApiResponse.success("Menu retrieved for " + day, menuService.getMenuByDay(day)));
    }

    @GetMapping("/day/{day}/meal/{meal}")
    @Operation(summary = "Get specific meal menu")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuByDayAndMeal(
            @PathVariable DayOfWeek day,
            @PathVariable MealType meal) {
        return ResponseEntity.ok(ApiResponse.success("Menu retrieved", menuService.getMenuByDayAndMeal(day, meal)));
    }

    // ========== Admin Endpoints ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create menu entry (Admin)")
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Menu created successfully", menuService.createMenu(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update menu by ID (Admin)")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Menu updated successfully", menuService.updateMenu(id, request)));
    }

    @PutMapping("/day/{day}/meal/{meal}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update menu by day and meal (Admin)")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuByDayAndMeal(
            @PathVariable DayOfWeek day,
            @PathVariable MealType meal,
            @Valid @RequestBody UpdateMenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Menu updated successfully", menuService.updateMenuByDayAndMeal(day, meal, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete menu entry (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.success("Menu deleted successfully", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get menu by ID (Admin)")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Menu retrieved", menuService.getMenuById(id)));
    }
}
