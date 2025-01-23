package yael.project.myApi.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yael.project.myApi.main.dao.BookingRepository;
import yael.project.myApi.main.dao.SeatRepository;
import yael.project.myApi.main.dao.ShowtimeRepository;
import yael.project.myApi.main.dto.BookingRequest;
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.Booking;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.model.Seat;
import yael.project.myApi.main.model.Showtime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Value("${max.seats}")
    private Integer maxSeats;

    public synchronized void bookTicket(BookingRequest bookingRequest) {
        Optional<Showtime> showtimeOptional = showtimeRepository.findById(bookingRequest.getShowtimeId());
        if (showtimeOptional.isEmpty()) {
            throw new IllegalArgumentException("Showtime not found.");
        }

        Showtime showtime = showtimeOptional.get();

        if (seatRepository.isSeatBooked(bookingRequest.getSeatNumber(), bookingRequest.getShowtimeId())) {
            throw new IllegalArgumentException("Seat is already booked for this showtime.");
        }

        if (showtime.getCurrentBookedSeats() == maxSeats) {
            throw new RuntimeException("No available seats left for this showtime.");
        }
            Booking booking = new Booking();
            booking.setUser(bookingRequest.getUser());
            booking.setMovie(showtime.getMovie());
            booking.setShowtime(showtime);
            booking.setSeatNumber(bookingRequest.getSeatNumber());
            booking.setPrice(showtime.getPrice());

            //we save this seat for this showtime
            Seat seat = new Seat();
            seat.setBooked(true);
            seat.setShowtime(showtime);
            seat.setSeatNumber(bookingRequest.getSeatNumber());
            seatRepository.save(seat);

            //we increment booked seats for this showtime by 1.
            Showtime showtimeToUpdate = showtimeRepository.getOne(showtime.getId());
            showtimeToUpdate.setCurrentBookedSeats(showtime.getCurrentBookedSeats() + 1);
            showtimeRepository.save(showtimeToUpdate);

            //booking is saved
            bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll().stream().collect(Collectors.toList());
        if (bookings != null) {
            return bookings;
        } else {
            throw new ResourceNotFoundException("No bookings found.");
        }
    }
}