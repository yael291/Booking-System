package yael.project.myApi.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Entity
@Data
public class Booking {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "SHOWTIME_ID", nullable = false) //showtime contains movie details
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID", nullable = false)
    private Movie movie;

    @Column(name = "SEAT_NUMBER")
    @NotNull(message = "seat number is required")
    private Integer seatNumber;

    @Column(name = "PRICE")
    @NotNull(message = "Price is required")
    private volatile Double price;

    public Booking() {
    }

    public Booking(User user, Showtime showtime, Movie movie, Integer seatNumber, Double price) {
        this.user = user;
        this.showtime = showtime;
        this.movie = movie;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}