package com.dormex.controller;

import com.dormex.dto.ApiResponse;
import com.dormex.dto.room.BlockResponse;
import com.dormex.dto.room.CreateBlockRequest;
import com.dormex.service.BlockService;
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
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Block Management", description = "Manage hostel blocks")
@SecurityRequirement(name = "Bearer Authentication")
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new block (Admin only)")
    public ResponseEntity<ApiResponse<BlockResponse>> createBlock(
            @Valid @RequestBody CreateBlockRequest request) {
        BlockResponse response = blockService.createBlock(request);
        return ResponseEntity.ok(ApiResponse.success("Block created", response));
    }

    @GetMapping
    @Operation(summary = "Get all blocks")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getAllBlocks() {
        List<BlockResponse> blocks = blockService.getAllBlocks();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active blocks only")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getActiveBlocks() {
        List<BlockResponse> blocks = blockService.getActiveBlocks();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get block by ID")
    public ResponseEntity<ApiResponse<BlockResponse>> getBlockById(@PathVariable Long id) {
        BlockResponse response = blockService.getBlockById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update block (Admin only)")
    public ResponseEntity<ApiResponse<BlockResponse>> updateBlock(
            @PathVariable Long id,
            @Valid @RequestBody CreateBlockRequest request) {
        BlockResponse response = blockService.updateBlock(id, request);
        return ResponseEntity.ok(ApiResponse.success("Block updated", response));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle block active status (Admin only)")
    public ResponseEntity<ApiResponse<BlockResponse>> toggleBlockStatus(@PathVariable Long id) {
        BlockResponse response = blockService.toggleBlockStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Status toggled", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete block (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteBlock(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.ok(ApiResponse.success("Block deleted", null));
    }
}
