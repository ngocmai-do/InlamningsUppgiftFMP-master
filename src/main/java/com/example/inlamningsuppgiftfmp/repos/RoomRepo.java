package com.example.inlamningsuppgiftfmp.repos;

import com.example.inlamningsuppgiftfmp.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room, Long> {
}
