package com.example.inlamningsuppgiftfmp.repos;

import com.example.inlamningsuppgiftfmp.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {

    boolean existsByCustomerId(Long costumerId);

    boolean existsByRoomId(Long roomId);

    @Query("SELECT b.room.id FROM Booking b WHERE b.startDate < :endDate AND b.endDate > :startDate")
    List<Long> findBookedRoomIds(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //check if new booking overlaps with any existing booking
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.startDate < :endDate AND b.endDate > :startDate")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //check if edited booking overlaps with any existing booking but excluding the current booking which is being edited
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.id <> :bookingId AND b.startDate < :endDate AND b.endDate > :startDate")
    boolean existsOverlappingBookingExcludingCurrent(@Param("roomId") Long roomId, @Param("bookingId") Long bookingId,
                                                     @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
