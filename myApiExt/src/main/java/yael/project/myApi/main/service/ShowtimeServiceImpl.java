package yael.project.myApi.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yael.project.myApi.main.dao.MoviesRepository;
import yael.project.myApi.main.dao.ShowtimeRepository;
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.model.Showtime;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MoviesRepository moviesRepository;

    public Showtime addShowtime(Showtime showtime) {
        Optional<Movie> movieExists = moviesRepository.findById(showtime.getMovie().getId());
        if (movieExists.isEmpty()) {
            throw new IllegalArgumentException("This movie does'nt exist.");
        }
        //check if this showtime overlaps with another in this theater
        boolean hasOverlap = showtimeRepository.existsOverlappingShowtime(
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime()
        );
        if (hasOverlap) {
            throw new IllegalArgumentException("Showtime overlaps with an existing showtime in the same theater.");
        }

        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, Showtime showtime) {
        Optional<Showtime> existingShowtime = showtimeRepository.findById(id);
        if (existingShowtime.isPresent()) {
            Showtime updatedShowtime = existingShowtime.get();
            updatedShowtime.setMovie(showtime.getMovie());
            updatedShowtime.setTheater(showtime.getTheater());
            //if we changed start time or end time of an existing showtime, we must check overlap with other showtines
            if (!showtime.getStartTime().equals(existingShowtime.get().getStartTime()) || !showtime.getEndTime().equals(existingShowtime.get().getEndTime())) {
                //  updatedShowtime.setStartTime(showtime.getStartTime());
                //  updatedShowtime.setEndTime(showtime.getEndTime());
                boolean hasOverlap = showtimeRepository.existsOverlappingShowtime(
                        showtime.getTheater(),
                        showtime.getStartTime(),
                        showtime.getEndTime()
                );

                if (hasOverlap) {
                    throw new IllegalArgumentException("Showtime overlaps with an existing showtime in the same theater.");
                } else {
                    updatedShowtime.setStartTime(showtime.getStartTime());
                    updatedShowtime.setEndTime(showtime.getEndTime());
                }
            }
            updatedShowtime.setPrice(showtime.getPrice());
            updatedShowtime.setCurrentBookedSeats(showtime.getCurrentBookedSeats());
            return showtimeRepository.save(updatedShowtime);
        }
        return null;
    }

    public Long delete(Long id) throws ResourceNotFoundException {
        Showtime showtimeFromDB = showtimeRepository.findById(Long.valueOf(id)).orElse(null);
        if (showtimeFromDB == null)
            throw new ResourceNotFoundException("Showtime not found with id " + id);
        showtimeRepository.deleteById(id);
        return id;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getShowtimeById(Long id) {
        Optional<Showtime> showtime = showtimeRepository.findById(id);
        if (showtime.isPresent()) {
            return showtime;
        } else {
            throw new ResourceNotFoundException("Showtime not found with id " + id);
        }

    }
}