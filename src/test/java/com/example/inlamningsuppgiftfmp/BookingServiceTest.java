package com.example.inlamningsuppgiftfmp;
import com.example.inlamningsuppgiftfmp.dtos.BookingDto;
import com.example.inlamningsuppgiftfmp.models.Booking;
import com.example.inlamningsuppgiftfmp.models.Room;
import com.example.inlamningsuppgiftfmp.models.RoomType;
import com.example.inlamningsuppgiftfmp.repos.BookingRepo;
import com.example.inlamningsuppgiftfmp.repos.RoomRepo;
import com.example.inlamningsuppgiftfmp.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {



    @Mock
    private BookingRepo bookingRepo;


    @Mock
    private RoomRepo roomRepo;


    @InjectMocks
    private BookingService bookingService;


    private String customerName = "Alice";
    private Long customerId = 1L;
    private Room room;
    private Booking booking;
    private BookingDto bookingDto;


    @BeforeEach
    void setUp() {


        room = new Room();
        room.setId(1L);
        room.setType(RoomType.SINGLE);


        booking = new Booking();
        booking.setId(1L);
        booking.setCustomerId(customerId);
        booking.setRoom(room);
        booking.setStartDate(LocalDate.of(2026, 6, 1));
        booking.setEndDate(LocalDate.of(2026, 6, 5));


        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setCustomerId(1L);
        bookingDto.setRoomId(1L);
        bookingDto.setStartDate(LocalDate.of(2026, 6, 1));
        bookingDto.setEndDate(LocalDate.of(2026, 6, 5));
    }


    @Test
    void getAllBookings_returnList(){
        Booking booking1 = new Booking();
        booking1.setId(2L);
        booking1.setCustomerId(customerId);
        booking1.setRoom(room);
        booking1.setStartDate(LocalDate.of(2027, 3, 2));
        booking1.setEndDate(LocalDate.of(2028, 2, 2));


        when(bookingRepo.findAll()).thenReturn(List.of(booking, booking1));
        List<BookingDto> result = bookingService.getAllBookings();
        assertEquals(2, result.size());
    }


    @Test
    void getBookingById_returnBooking(){


        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        Optional<BookingDto> result = bookingService.getBookingById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getCustomerId());


    }


    @Test
    void createBooking_success() {


        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepo.existsOverlappingBooking(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(false);
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);


        BookingDto result = bookingService.createBooking(bookingDto);


        assertEquals(1L, result.getCustomerId());
        assertEquals(1L, result.getRoomId());


        verify(bookingRepo).save(any(Booking.class));
    }


    @Test
    void createBooking_overlappingBooking() {


        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));


        when(bookingRepo.existsOverlappingBooking(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(true);


        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.createBooking(bookingDto)
        );


        assertEquals("Room is already booked for selected date", exception.getMessage());
    }


    @Test
    void createBooking_invalidDates() {


        bookingDto.setStartDate(LocalDate.of(2026, 6, 10));
        bookingDto.setEndDate(LocalDate.of(2026, 6, 5));


        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));


        when(bookingRepo.existsOverlappingBooking(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(false);


        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.createBooking(bookingDto)
        );


        assertEquals("Check-in date must be before check-out date", exception.getMessage());
    }


    @Test
    void updateBooking_success() {


        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));


        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));


        when(bookingRepo.existsOverlappingBookingExcludingCurrent(
                anyLong(),
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(false);


        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);


        Optional<BookingDto> result = bookingService.updateBooking(bookingDto);


        assertTrue(result.isPresent());


        verify(bookingRepo).save(any(Booking.class));
    }


    @Test
    void updateBooking_bookingNotFound() {


        when(bookingRepo.findById(1L)).thenReturn(Optional.empty());


        Optional<BookingDto> result = bookingService.updateBooking(bookingDto);


        assertTrue(result.isEmpty());
    }


    @Test
    void updateBooking_invalidDates() {


        bookingDto.setStartDate(LocalDate.of(2026, 7, 10));
        bookingDto.setEndDate(LocalDate.of(2026, 7, 1));


        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));


        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));


        when(bookingRepo.existsOverlappingBookingExcludingCurrent(
                anyLong(),
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(false);


        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bookingService.updateBooking(bookingDto)
        );


        assertEquals("Check-in date must be before check-out date", exception.getMessage());
    }


    @Test
    void deleteBooking_callsDeleteById() {


        bookingService.deleteBooking(1L);


        verify(bookingRepo).deleteById(1L);
    }


}


