package com.dormex.service;

import com.dormex.dto.room.BlockResponse;
import com.dormex.dto.room.CreateBlockRequest;
import com.dormex.entity.Block;
import com.dormex.entity.enums.RoomStatus;
import com.dormex.exception.BadRequestException;
import com.dormex.exception.ResourceNotFoundException;
import com.dormex.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;

    @Transactional
    public BlockResponse createBlock(CreateBlockRequest request) {
        if (blockRepository.existsByName(request.getName())) {
            throw new BadRequestException("Block name already exists");
        }

        Block block = Block.builder()
            .name(request.getName())
            .description(request.getDescription())
            .totalFloors(request.getTotalFloors())
            .active(true)
            .build();

        block = blockRepository.save(block);
        return mapToResponse(block);
    }

    @Transactional(readOnly = true)
    public BlockResponse getBlockById(Long id) {
        Block block = findBlockById(id);
        return mapToResponse(block);
    }

    @Transactional(readOnly = true)
    public List<BlockResponse> getAllBlocks() {
        return blockRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<BlockResponse> getActiveBlocks() {
        return blockRepository.findByActiveTrue().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public BlockResponse updateBlock(Long id, CreateBlockRequest request) {
        Block block = findBlockById(id);

        if (!block.getName().equals(request.getName()) && 
            blockRepository.existsByName(request.getName())) {
            throw new BadRequestException("Block name already exists");
        }

        block.setName(request.getName());
        block.setDescription(request.getDescription());
        block.setTotalFloors(request.getTotalFloors());

        block = blockRepository.save(block);
        return mapToResponse(block);
    }

    @Transactional
    public BlockResponse toggleBlockStatus(Long id) {
        Block block = findBlockById(id);
        block.setActive(!block.isActive());
        block = blockRepository.save(block);
        return mapToResponse(block);
    }

    @Transactional
    public void deleteBlock(Long id) {
        Block block = findBlockById(id);
        if (!block.getRooms().isEmpty()) {
            throw new BadRequestException("Cannot delete block with existing rooms");
        }
        blockRepository.delete(block);
    }

    Block findBlockById(Long id) {
        return blockRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Block", "id", id));
    }

    private BlockResponse mapToResponse(Block block) {
        int totalRooms = block.getRooms().size();
        int occupiedRooms = (int) block.getRooms().stream()
            .filter(r -> r.getStatus() == RoomStatus.OCCUPIED || r.getStatus() == RoomStatus.FULL)
            .count();

        return BlockResponse.builder()
            .id(block.getId())
            .name(block.getName())
            .description(block.getDescription())
            .totalFloors(block.getTotalFloors())
            .active(block.isActive())
            .totalRooms(totalRooms)
            .occupiedRooms(occupiedRooms)
            .createdAt(block.getCreatedAt())
            .build();
    }
}
