package com.example.inlamningsuppgiftfmp.models;

public enum MaxExtraBed {
    NONE(0),
    ONE(1),
    TWO(2);

    private final int value;

    MaxExtraBed(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
