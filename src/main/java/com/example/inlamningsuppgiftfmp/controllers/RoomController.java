package com.example.inlamningsuppgiftfmp.controllers;

import com.example.inlamningsuppgiftfmp.dtos.RoomDto;
import com.example.inlamningsuppgiftfmp.services.RoomService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @RequestMapping("/all")
    public String getAll(Model model) {

        List<RoomDto> roomDtoList = roomService.getAllRooms();

        model.addAttribute("allRooms", roomDtoList);
        model.addAttribute("id", "ID");
        model.addAttribute("type", "Type");
        model.addAttribute("maxExtraBed", "Max Extra Bed");
        model.addAttribute("roomTitle", "All Rooms");

        return "room";
    }


    @RequestMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes){
        boolean deleted = roomService.deleteRoom(id);

        if (!deleted){
            redirectAttributes.addFlashAttribute("error", "Cannot delete room with existing bookings");
        } else {
            redirectAttributes.addFlashAttribute("success", "Room deleted successfully");
        }

        return "redirect:/room/all";
    }


    @RequestMapping("/edit/{id}")
    public String createEditRoomForm(@PathVariable Long id, Model model) {
        Optional<RoomDto> optionalRoom = roomService.getRoomById(id);

        if (optionalRoom.isEmpty()) {
            model.addAttribute("error", "Room not found");
            return "redirect:/room/all";
        }

        model.addAttribute("room", optionalRoom.get());

        return "editRoomForm";
    }


    @RequestMapping("/new")
    public String createAddRoomForm(Model model) {
        model.addAttribute("room", new RoomDto());
        return "addRoomForm";
    }


    @PostMapping("/update")
    public String saveRoom(@Valid RoomDto roomDto) {
        roomService.saveRoom(roomDto);
        return "redirect:/room/all";
    }

    @RequestMapping("/search")
    public String createSearchRoomForm() {
        return "searchRoom";
    }

    @PostMapping("/searchedRoom")
    public String getAvailableRoom(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   @RequestParam("numberOfGuest") int guests,
                                   Model model){
        List<RoomDto> roomDtoList = roomService.searchAvailableRooms(startDate, endDate, guests);

        model.addAttribute("allRooms", roomDtoList);
        model.addAttribute("id", "ID");
        model.addAttribute("type", "Type");
        model.addAttribute("maxExtraBed", "Max Extra Bed");
        model.addAttribute("roomTitle", "All Rooms");

        return "showSearchedRoom";
    }
}
