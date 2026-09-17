package com.example.inlamningsuppgiftfmp.models;

import jakarta.persistence.*;

@Entity
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Enumerated
    private MaxExtraBed maxExtraBed;

    public Room(){}

    public Room(RoomType type, MaxExtraBed maxExtraBed) {
        this.type = type;
        this.maxExtraBed = maxExtraBed;
    }

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
