package com.example.inlamningsuppgiftfmp.services;

import com.example.inlamningsuppgiftfmp.dtos.BookingDto;
import com.example.inlamningsuppgiftfmp.models.Booking;
import com.example.inlamningsuppgiftfmp.models.Room;
import com.example.inlamningsuppgiftfmp.repos.BookingRepo;
import com.example.inlamningsuppgiftfmp.repos.RoomRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.temporal.ChronoUnit;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;
    private final RoomRepo roomRepo;

    public BookingService(BookingRepo bookingRepo, RoomRepo roomRepo) {
        this.bookingRepo = bookingRepo;
        this.roomRepo = roomRepo;
    }

    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<BookingDto> getBookingById(Long id) {
        return bookingRepo.findById(id)
                .map(this::toDto);
    }


    public BookingDto createBooking(BookingDto bookingDto) {

        Room room = roomRepo.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean overlaps = bookingRepo.existsOverlappingBooking(
                room.getId(),
                bookingDto.getStartDate(),
                bookingDto.getEndDate()
        );

        if (overlaps) {
            throw new RuntimeException("Room is already booked for selected date");
        }

        if (bookingDto.getStartDate().isAfter(bookingDto.getEndDate()) ||
                bookingDto.getStartDate().isEqual(bookingDto.getEndDate())) {

            throw new RuntimeException("Check-in date must be before check-out date");
        }

        Booking booking = new Booking();
        booking.setCustomerId(bookingDto.getCustomerId());
        booking.setRoom(room);
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());

        return toDto(bookingRepo.save(booking));
    }


    public Optional<BookingDto> updateBooking(BookingDto bookingDto) {

        return bookingRepo.findById(bookingDto.getId()).map(existing -> {

            Room room = roomRepo.findById(bookingDto.getRoomId())
                    .orElseThrow();


            boolean overlaps = bookingRepo.existsOverlappingBookingExcludingCurrent(
                    room.getId(),
                    bookingDto.getId(),
                    bookingDto.getStartDate(),
                    bookingDto.getEndDate()
            );

            if (overlaps &&
                    !(existing.getRoom().getId().equals(room.getId())
                            && existing.getStartDate().equals(bookingDto.getStartDate())
                            && existing.getEndDate().equals(bookingDto.getEndDate()))) {

                throw new RuntimeException("Room is already booked for selected date");
            }

            if (bookingDto.getStartDate().isAfter(bookingDto.getEndDate()) ||
                    bookingDto.getStartDate().isEqual(bookingDto.getEndDate())) {

                throw new RuntimeException("Check-in date must be before check-out date");
            }

            existing.setCustomerId(bookingDto.getCustomerId());
            existing.setRoom(room);
            existing.setStartDate(bookingDto.getStartDate());
            existing.setEndDate(bookingDto.getEndDate());

            return toDto(bookingRepo.save(existing));
        });
    }


    public void deleteBooking(Long id) {
        bookingRepo.deleteById(id);
    }

    public boolean bookingExist(Long id) {
        return bookingRepo.existsByCustomerId(id);
    }

    private BookingDto toDto(Booking booking) {

        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomerId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomType(booking.getRoom().getType());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setNumberOfNights(ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()));

        return dto;
    }


}