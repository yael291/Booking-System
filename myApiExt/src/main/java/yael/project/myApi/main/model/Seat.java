package yael.project.myApi.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer seatNumber;

    private volatile boolean isBooked; //can be atomic

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false) //showtime contains movie details
    private Showtime showtime;

}