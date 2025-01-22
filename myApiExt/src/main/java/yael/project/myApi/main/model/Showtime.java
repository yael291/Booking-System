package yael.project.myApi.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_theater_time", columnList = "theater, startTime, endTime")
})
@Data
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    private String theater;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(name = "PRICE")
    @NotNull
    private Double price;

    @Column(name = "CURRENT_BOOKED_SEATS")
    @NotNull
    private Integer currentBookedSeats;

    public Showtime(Long id, Movie movie, String theater, LocalDateTime startTime, LocalDateTime endTime, Double price, Integer currentBookedSeats) {
        this.id = id;
        this.movie = movie;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.currentBookedSeats = currentBookedSeats;
    }
    public Showtime(){}

}



