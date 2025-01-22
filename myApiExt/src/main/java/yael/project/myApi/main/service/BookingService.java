package yael.project.myApi.main.service;

import yael.project.myApi.main.dto.BookingRequest;
import yael.project.myApi.main.model.Booking;

import java.util.List;

public interface BookingService {
    void bookTicket(BookingRequest bookingRequest);
    List<Booking> getAllBookings();
}

