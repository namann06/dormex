package com.dormex.repository;

import com.dormex.entity.Room;
import com.dormex.entity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByBlockId(Long blockId);

    List<Room> findByBlockIdAndFloor(Long blockId, Integer floor);

    List<Room> findByStatus(RoomStatus status);

    Optional<Room> findByBlockIdAndRoomNumber(Long blockId, String roomNumber);

    boolean existsByBlockIdAndRoomNumber(Long blockId, String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.currentOccupancy < r.capacity AND r.status = 'AVAILABLE'")
    List<Room> findVacantRooms();

    @Query("SELECT r FROM Room r WHERE r.block.id = :blockId AND r.currentOccupancy < r.capacity AND r.status = 'AVAILABLE'")
    List<Room> findVacantRoomsByBlock(@Param("blockId") Long blockId);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = 'AVAILABLE' OR r.status = 'OCCUPIED'")
    long countAvailableRooms();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = 'FULL'")
    long countFullRooms();

    @Query("SELECT SUM(r.capacity) FROM Room r WHERE r.status != 'UNDER_MAINTENANCE'")
    Long getTotalCapacity();

    @Query("SELECT SUM(r.currentOccupancy) FROM Room r")
    Long getTotalOccupancy();
}
