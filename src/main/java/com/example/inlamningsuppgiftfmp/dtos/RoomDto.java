package com.example.inlamningsuppgiftfmp.dtos;

import com.example.inlamningsuppgiftfmp.models.MaxExtraBed;
import com.example.inlamningsuppgiftfmp.models.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RoomDto {
    private Long id;

    @NotNull(message = "Room type must be filled")
    private RoomType type;

    @NotNull(message = "Number of max extra bed must be filled")
    private MaxExtraBed maxExtraBed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public MaxExtraBed getMaxExtraBed() {
        return maxExtraBed;
    }

    public void setMaxExtraBed(MaxExtraBed maxExtraBed) {
        this.maxExtraBed = maxExtraBed;
    }

}
