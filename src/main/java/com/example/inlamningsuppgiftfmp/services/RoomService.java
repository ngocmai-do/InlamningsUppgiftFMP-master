package com.example.inlamningsuppgiftfmp.services;

import com.example.inlamningsuppgiftfmp.dtos.RoomDto;
import com.example.inlamningsuppgiftfmp.models.Room;
import com.example.inlamningsuppgiftfmp.models.RoomType;
import com.example.inlamningsuppgiftfmp.repos.BookingRepo;
import com.example.inlamningsuppgiftfmp.repos.RoomRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class RoomService {
    private final RoomRepo roomRepo;
    private final BookingRepo bookingRepo;


    public RoomService(RoomRepo roomRepo, BookingRepo bookingRepo) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
    }

    public List<RoomDto> getAllRooms() {
        return roomRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<RoomDto> getRoomById(Long id) {
        return roomRepo.findById(id).map(this::toDto);
    }

    public RoomDto saveRoom(RoomDto dto) {
        Room saved = roomRepo.save(toEntity(dto));
        return toDto(saved);
    }

    public Optional<RoomDto> updateRoom(Long id, RoomDto dto) {
        return roomRepo.findById(id).map(existing -> {
            existing.setType(dto.getType());
            existing.setMaxExtraBed(dto.getMaxExtraBed());
            return toDto(roomRepo.save(existing));
        });
    }

    public boolean deleteRoom(Long id) {
        boolean hasBookings = bookingRepo.existsByRoomId(id);
        if (hasBookings) {
            return false;
        }
        roomRepo.deleteById(id);
        return true;
    }

    public List<RoomDto> searchAvailableRooms(LocalDate startDate, LocalDate endDate, int guests) {
        List<Long> bookedRoomIds = bookingRepo.findBookedRoomIds(startDate, endDate);

        return roomRepo.findAll()
                .stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .filter(room -> getMaxGuests(room) >= guests)
                .map(this::toDto)
                .toList();
    }

    private int getMaxGuests(Room room) {
        int base = room.getType().equals(RoomType.SINGLE) ? 1 : 2;
        return base + room.getMaxExtraBed().getValue();
    }

    public RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setType(room.getType());
        dto.setMaxExtraBed(room.getMaxExtraBed());
        return dto;
    }

    public Room toEntity(RoomDto dto) {
        Room room = new Room();
        if (dto.getId() != null) {
            room.setId(dto.getId());
        }
        room.setType(dto.getType());
        room.setMaxExtraBed(dto.getMaxExtraBed());
        return room;
    }

}
