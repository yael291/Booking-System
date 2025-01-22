package yael.project.myApi.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yael.project.myApi.main.model.Showtime;
import yael.project.myApi.main.service.ShowtimeService;

import java.util.Optional;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @PostMapping("/add")
    public ResponseEntity<Object> addShowtime(@RequestBody Showtime showtime) {
        try {
            return ResponseEntity.ok(showtimeService.addShowtime(showtime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateShowtime(@PathVariable Long id, @RequestBody Showtime showtime) {
        try {
            Showtime updatedShowtime = showtimeService.updateShowtime(id, showtime);
            return updatedShowtime != null ? ResponseEntity.ok(updatedShowtime) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteShowtime(@PathVariable Long id) {
        try {
            if (id == null)
                return ResponseEntity.badRequest().body("The provided showtime's id is not valid");
            return ResponseEntity.ok().body("Showtime [" + showtimeService.delete(id) + "] deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllShowtimes() {
        try {
            return ResponseEntity.ok(showtimeService.getAllShowtimes());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getShowtimeById(@PathVariable Long id) {
        try {
            Optional<Showtime> showtime = showtimeService.getShowtimeById(id);
            return ResponseEntity.ok(showtime.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}