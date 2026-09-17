package com.example.inlamningsuppgiftfmp;

import com.example.inlamningsuppgiftfmp.models.*;
import com.example.inlamningsuppgiftfmp.repos.BookingRepo;
import com.example.inlamningsuppgiftfmp.repos.RoomRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootApplication
public class InlamningsUppgiftFmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(InlamningsUppgiftFmpApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public CommandLineRunner demo(RoomRepo roomRepo, BookingRepo bookingRepo){
        return (args) -> {


            Room r1 = new Room(RoomType.SINGLE, MaxExtraBed.NONE);
            Room r2 = new Room(RoomType.DOUBLE,MaxExtraBed.ONE);
            Room r3 = new Room(RoomType.DOUBLE,MaxExtraBed.TWO);
            Room r4 = new Room(RoomType.SINGLE,MaxExtraBed.NONE);
            Room r5 = new Room(RoomType.DOUBLE,MaxExtraBed.TWO);

            roomRepo.save(r1);
            roomRepo.save(r2);
            roomRepo.save(r3);
            roomRepo.save(r4);
            roomRepo.save(r5);

            Booking b1 = new Booking(1L,r5, LocalDate.of(2026,05,30),LocalDate.of(2026,06,15));
            Booking b2 = new Booking(2L,r4, LocalDate.of(2026,06,01),LocalDate.of(2026,06,10));
            Booking b3 = new Booking(3L,r2, LocalDate.of(2026,10,18),LocalDate.of(2026,12,5));

            bookingRepo.save(b1);
            bookingRepo.save(b2);
            bookingRepo.save(b3);
        };
    }
}
