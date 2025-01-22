package yael.project.myApi.main.service;

import org.springframework.http.ResponseEntity;
import yael.project.myApi.main.model.Showtime;

import java.util.List;
import java.util.Optional;

public interface ShowtimeService {


    Showtime addShowtime(Showtime showtime);

    Showtime updateShowtime(Long id, Showtime showtime);

    Long delete(Long id);

    List<Showtime> getAllShowtimes();

    Optional<Showtime> getShowtimeById(Long id);
}
