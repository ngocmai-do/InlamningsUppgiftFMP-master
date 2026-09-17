package com.example.inlamningsuppgiftfmp.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    private Long customerId;

    @ManyToOne
    @JoinColumn
    private Room room;

    private LocalDate startDate;
    private LocalDate endDate;

    public Booking(){}

    public Booking(Long customerId, Room room, LocalDate startDate, LocalDate endDate) {
        this.customerId = customerId;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
