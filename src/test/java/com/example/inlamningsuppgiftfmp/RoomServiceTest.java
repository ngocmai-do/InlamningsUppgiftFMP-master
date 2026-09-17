package com.example.inlamningsuppgiftfmp;

import com.example.inlamningsuppgiftfmp.dtos.RoomDto;
import com.example.inlamningsuppgiftfmp.models.MaxExtraBed;
import com.example.inlamningsuppgiftfmp.models.Room;
import com.example.inlamningsuppgiftfmp.models.RoomType;
import com.example.inlamningsuppgiftfmp.repos.BookingRepo;
import com.example.inlamningsuppgiftfmp.repos.RoomRepo;
import com.example.inlamningsuppgiftfmp.services.RoomService;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepo roomRepo;

    @Mock
    private BookingRepo bookingRepo;

    @InjectMocks
    private RoomService roomService;

    private Room enkelrum;
    private Room dubbelrum;
    private RoomDto roomDto;

    @BeforeEach
    void setUp() {
        enkelrum = new Room(RoomType.SINGLE, MaxExtraBed.NONE);
        enkelrum.setId(1L);

        dubbelrum = new Room(RoomType.DOUBLE, MaxExtraBed.TWO);
        dubbelrum.setId(2L);

        roomDto = new RoomDto();
        roomDto.setType(RoomType.SINGLE);
        roomDto.setMaxExtraBed(MaxExtraBed.NONE);
    }

    @Test
    void getAllRooms_returnsList() {
        when(roomRepo.findAll()).thenReturn(List.of(enkelrum, dubbelrum));

        List<RoomDto> result = roomService.getAllRooms();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo(RoomType.SINGLE);
        assertThat(result.get(1).getType()).isEqualTo(RoomType.DOUBLE);
    }

    @Test
    void getRoomById_returnsDto_whenFound() {
        when(roomRepo.findById(1L)).thenReturn(Optional.of(enkelrum));

        Optional<RoomDto> result = roomService.getRoomById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(RoomType.SINGLE);
    }
    @Test
    void saveRoom_savesAndReturnsDto() {
        when(roomRepo.save(any(Room.class))).thenReturn(enkelrum);

        RoomDto result = roomService.saveRoom(roomDto);

        assertThat(result.getType()).isEqualTo(RoomType.SINGLE);
        verify(roomRepo, times(1)).save(any(Room.class));
    }

    @Test
    void updateRoom_updatesFields_whenFound() {
        RoomDto updatedDto = new RoomDto();
        updatedDto.setType(RoomType.DOUBLE);
        updatedDto.setMaxExtraBed(MaxExtraBed.ONE);

        when(roomRepo.findById(1L)).thenReturn(Optional.of(enkelrum));
        when(roomRepo.save(any(Room.class))).thenReturn(enkelrum);

        Optional<RoomDto> result = roomService.updateRoom(1L, updatedDto);

        assertThat(result).isPresent();
        verify(roomRepo, times(1)).save(any(Room.class));
    }

    @Test
    void deleteRoom_deletesAndReturnsTrue_whenIngaBokningar() {
        when(bookingRepo.existsByRoomId(1L)).thenReturn(false);

        boolean result = roomService.deleteRoom(1L);

        assertThat(result).isTrue();
        verify(roomRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteRoom_returnsFalse_whenHarBokningar() {
        when(bookingRepo.existsByRoomId(1L)).thenReturn(true);

        boolean result = roomService.deleteRoom(1L);

        assertThat(result).isFalse();
        verify(roomRepo, never()).deleteById(any());
    }

    @Test
    void searchAvailableRooms_exkluderarBokatRum() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 5);

        when(bookingRepo.findBookedRoomIds(start, end)).thenReturn(List.of(1L));
        when(roomRepo.findAll()).thenReturn(List.of(enkelrum));

        List<RoomDto> result = roomService.searchAvailableRooms(start, end, 1);

        assertThat(result).isEmpty();
    }

    @Test
    void searchAvailableRooms_exkluderarRumMedForLitenKapacitet() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 5);

        when(bookingRepo.findBookedRoomIds(start, end)).thenReturn(List.of());
        when(roomRepo.findAll()).thenReturn(List.of(enkelrum)); // SINGLE + NONE = 1 plats

        List<RoomDto> result = roomService.searchAvailableRooms(start, end, 3);

        assertThat(result).isEmpty();
    }










}
