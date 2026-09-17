package com.example.inlamningsuppgiftfmp.dtos;

import com.example.inlamningsuppgiftfmp.models.RoomType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public class BookingDto {

    private Long id;

    @NotNull(message = "Customer must be selected")
    private Long customerId;

    private String customerName;

    @NotNull(message = "Room must be selected")
    private Long roomId;

    private RoomType roomType;

    @NotNull(message = "Check-in date must be selected")
    private LocalDate startDate;

    @NotNull(message = "Check-out date must be selected")
    private LocalDate endDate;

    private Long numberOfNights;

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
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

    public Long getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(Long numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

}