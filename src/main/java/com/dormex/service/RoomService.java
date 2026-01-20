package com.dormex.service;

import com.dormex.dto.room.CreateRoomRequest;
import com.dormex.dto.room.RoomResponse;
import com.dormex.dto.room.UpdateRoomRequest;
import com.dormex.entity.Block;
import com.dormex.entity.Room;
import com.dormex.entity.enums.RoomStatus;
import com.dormex.exception.BadRequestException;
import com.dormex.exception.ResourceNotFoundException;
import com.dormex.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BlockService blockService;

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        Block block = blockService.findBlockById(request.getBlockId());

        if (roomRepository.existsByBlockIdAndRoomNumber(request.getBlockId(), request.getRoomNumber())) {
            throw new BadRequestException("Room number already exists in this block");
        }

        Room room = Room.builder()
            .block(block)
            .roomNumber(request.getRoomNumber())
            .floor(request.getFloor())
            .capacity(request.getCapacity() != null ? request.getCapacity() : 1)
            .currentOccupancy(0)
            .status(RoomStatus.AVAILABLE)
            .roomType(request.getRoomType())
            .amenities(request.getAmenities())
            .build();

        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = findRoomById(id);
        return mapToResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByBlock(Long blockId) {
        return roomRepository.findByBlockId(blockId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByBlockAndFloor(Long blockId, Integer floor) {
        return roomRepository.findByBlockIdAndFloor(blockId, floor).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getVacantRooms() {
        return roomRepository.findVacantRooms().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getVacantRoomsByBlock(Long blockId) {
        return roomRepository.findVacantRoomsByBlock(blockId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = findRoomById(id);

        if (request.getCapacity() != null) {
            if (request.getCapacity() < room.getCurrentOccupancy()) {
                throw new BadRequestException("Capacity cannot be less than current occupancy");
            }
            room.setCapacity(request.getCapacity());
        }
        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getAmenities() != null) room.setAmenities(request.getAmenities());

        room.updateStatus();
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public RoomResponse updateRoomStatus(Long id, RoomStatus status) {
        Room room = findRoomById(id);
        room.setStatus(status);
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public RoomResponse incrementOccupancy(Long id) {
        Room room = findRoomById(id);

        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            throw new BadRequestException("Room is at full capacity");
        }

        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        room.updateStatus();
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public RoomResponse decrementOccupancy(Long id) {
        Room room = findRoomById(id);

        if (room.getCurrentOccupancy() <= 0) {
            throw new BadRequestException("Room is already empty");
        }

        room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
        room.updateStatus();
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = findRoomById(id);

        if (room.getCurrentOccupancy() > 0) {
            throw new BadRequestException("Cannot delete room with occupants");
        }

        roomRepository.delete(room);
    }

    Room findRoomById(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    private RoomResponse mapToResponse(Room room) {
        return RoomResponse.builder()
            .id(room.getId())
            .blockId(room.getBlock().getId())
            .blockName(room.getBlock().getName())
            .roomNumber(room.getRoomNumber())
            .floor(room.getFloor())
            .capacity(room.getCapacity())
            .currentOccupancy(room.getCurrentOccupancy())
            .availableSlots(room.getCapacity() - room.getCurrentOccupancy())
            .status(room.getStatus())
            .roomType(room.getRoomType())
            .amenities(room.getAmenities())
            .createdAt(room.getCreatedAt())
            .build();
    }
}
