package com.example.inlamningsuppgiftfmp.controllers;

import com.example.inlamningsuppgiftfmp.dtos.BookingDto;
import com.example.inlamningsuppgiftfmp.services.BookingService;
import com.example.inlamningsuppgiftfmp.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final RestTemplate restTemplate;



    @RequestMapping("/all")
    public String getAllBooking(Model model) {

        List<BookingDto> bookingDtoList = bookingService.getAllBookings();

        try {
            // getting all the information of all customers:
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://customerservice:8081/customers/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            List<Map<String, Object>> customers = response.getBody();

            // mapping the customer's information into a map with id and name:
            Map<Long, String> customerNames = customers.stream()
                    .collect(Collectors.toMap(
                            c -> Long.valueOf(c.get("id").toString()),
                            c -> (String) c.get("name")
                    ));

            // adding the name to the list of bookings:
            bookingDtoList.forEach(b ->
                    b.setCustomerName(customerNames.getOrDefault(b.getCustomerId(), "Unknown"))
            );
        } catch (RestClientException e) {        // if customer-service is down — show IDs instead of crashing the whole page
            bookingDtoList.forEach(b ->
                    b.setCustomerName("Customer #" + b.getCustomerId() + " (unavailable)")
            );
        }

        model.addAttribute("allBookings", bookingDtoList);
        model.addAttribute("customerName", "Customer Name");
        model.addAttribute("roomId", "Room ID");
        model.addAttribute("roomType", "Room Type");
        model.addAttribute("startDate","Check-in Date");
        model.addAttribute("endDate","Check-out Date");
        model.addAttribute("numberOfNights","Nights");
        model.addAttribute("bookingTitle", "All Bookings");

        return "booking";
    }


    @RequestMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id){
        bookingService.deleteBooking(id);
        return "redirect:/booking/all";
    }

    // customer service calls this function to check if a customer has an existing booking before deleting
    @GetMapping("/customer/{id}/exists")
    public ResponseEntity<Boolean> bookingExists(@PathVariable Long id) {
        boolean exists = bookingService.bookingExist(id);
        return ResponseEntity.ok(exists);
    }


    @RequestMapping("/edit/{id}")
    public String createEditBookingForm(@PathVariable Long id, Model model) {
        Optional<BookingDto> optionalBooking = bookingService.getBookingById(id);

        if (optionalBooking.isEmpty()) {
            model.addAttribute("error", "Booking not found");
            return "redirect:/booking/all";
        }

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://customerservice:8081/customers/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            model.addAttribute("customers", response.getBody());
        } catch (RestClientException e) {
            model.addAttribute("customers", List.of());
            model.addAttribute("error", "Customer service is currently unavailable — cannot load customer list.");
        }

        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("booking", optionalBooking.get());

        return "editBookingForm";
    }


    @PostMapping("/update")
    public String updateEditedBooking(@Valid @ModelAttribute("booking") BookingDto bookingDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            model.addAttribute("errorMsg", firstError);
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "editBookingForm";
        }

        try {
            boolean customerExists = restTemplate.getForObject(
                    "http://customerservice:8081/customers/" + bookingDto.getCustomerId(),
                    Object.class
            ) != null;

            if (!customerExists) {
                model.addAttribute("errorMsg", "Selected customer does not exist.");
                model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
                model.addAttribute("rooms", roomService.getAllRooms());
                return "editBookingForm";
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("errorMsg", "Selected customer does not exist.");
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "editBookingForm";
        } catch (RestClientException e) {
            model.addAttribute("errorMsg", "Customer service is currently unavailable. Please try again later.");
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "editBookingForm";
        }

        try {
            bookingService.updateBooking(bookingDto);
            redirectAttributes.addFlashAttribute("success", "Booking updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/booking/all";
    }

    private List<Map<String, Object>> fetchAllCustomersOrEmpty(Model model) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://customerservice:8081/customers/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            return List.of();
        }
    }


    @RequestMapping("/new")
    public String createAddBookingForm(Model model) {
        model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("booking", new BookingDto());
        return "addBookingForm";
    }


    @PostMapping("/create")
    public String createNewBooking(@Valid @ModelAttribute("booking") BookingDto bookingDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            model.addAttribute("errorMsg", firstError);
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "addBookingForm";
        }

        try {
            boolean customerExists = restTemplate.getForObject(
                    "http://customerservice:8081/customers/" + bookingDto.getCustomerId(),
                    Object.class
            ) != null;

            if (!customerExists) {
                model.addAttribute("errorMsg", "Selected customer does not exist.");
                model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
                model.addAttribute("rooms", roomService.getAllRooms());
                return "addBookingForm";
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("errorMsg", "Selected customer does not exist.");
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "addBookingForm";
        } catch (RestClientException e) {
            model.addAttribute("errorMsg", "Customer service is currently unavailable. Please try again later.");
            model.addAttribute("customers", fetchAllCustomersOrEmpty(model));
            model.addAttribute("rooms", roomService.getAllRooms());
            return "addBookingForm";
        }

        try {
            bookingService.createBooking(bookingDto);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/booking/all";
    }


}
