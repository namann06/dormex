package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.room.CreateRoomRequest;
import com.dormex.dto.room.RoomResponse;
import com.dormex.dto.room.UpdateRoomRequest;
import com.dormex.entity.enums.RoomStatus;
import com.dormex.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "Manage hostel rooms")
@SecurityRequirement(name = "Bearer Authentication")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new room (Admin only)")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.success("Room created", response));
    }

    @GetMapping
    @Operation(summary = "Get all rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        RoomResponse response = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/block/{blockId}")
    @Operation(summary = "Get rooms by block")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByBlock(@PathVariable Long blockId) {
        List<RoomResponse> rooms = roomService.getRoomsByBlock(blockId);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/block/{blockId}/floor/{floor}")
    @Operation(summary = "Get rooms by block and floor")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByBlockAndFloor(
            @PathVariable Long blockId, @PathVariable Integer floor) {
        List<RoomResponse> rooms = roomService.getRoomsByBlockAndFloor(blockId, floor);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/vacant")
    @Operation(summary = "Get all vacant rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getVacantRooms() {
        List<RoomResponse> rooms = roomService.getVacantRooms();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/vacant/block/{blockId}")
    @Operation(summary = "Get vacant rooms in a block")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getVacantRoomsByBlock(@PathVariable Long blockId) {
        List<RoomResponse> rooms = roomService.getVacantRoomsByBlock(blockId);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room (Admin only)")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room status (Admin only)")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        RoomResponse response = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted", null));
    }
}
