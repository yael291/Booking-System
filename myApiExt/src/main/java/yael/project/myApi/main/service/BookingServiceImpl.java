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

    public void bookTicket(BookingRequest bookingRequest) {
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
        Booking booking = new Booking(bookingRequest.getUser(), showtime, showtime.getMovie(), bookingRequest.getSeatNumber(), showtime.getPrice());
        //we create a seat for this showtime
        Seat seat = new Seat();
        seat.setShowtime(showtime);
        seat.setSeatNumber(bookingRequest.getSeatNumber());
        Showtime showtimeToUpdate = showtimeRepository.getOne(showtime.getId());
        //we book seat, and we increment booked seats for this showtime by 1(Atomic integer can also be used)
        synchronized (this) {
            seat.setBooked(true);
            showtimeToUpdate.setCurrentBookedSeats(showtime.getCurrentBookedSeats() + 1);
        }
        seatRepository.save(seat);
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