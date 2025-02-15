package yael.project.myApi.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yael.project.myApi.main.dto.BookingRequest;
import yael.project.myApi.main.service.BookingService;


@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<String> bookTicket(@RequestBody BookingRequest bookingRequest) {
        bookingService.bookTicket(bookingRequest);
        return ResponseEntity.ok("Ticket booked successfully!");
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
