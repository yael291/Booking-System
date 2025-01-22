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
        return ResponseEntity.ok(showtimeService.addShowtime(showtime));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateShowtime(@PathVariable Long id, @RequestBody Showtime showtime) {
        Showtime updatedShowtime = showtimeService.updateShowtime(id, showtime);
        return updatedShowtime != null ? ResponseEntity.ok(updatedShowtime) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteShowtime(@PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().body("The provided showtime's id is not valid");
        return ResponseEntity.ok().body("Showtime [" + showtimeService.delete(id) + "] deleted successfully.");

    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getShowtimeById(@PathVariable Long id) {
        Optional<Showtime> showtime = showtimeService.getShowtimeById(id);
        return ResponseEntity.ok(showtime.get());
    }
}