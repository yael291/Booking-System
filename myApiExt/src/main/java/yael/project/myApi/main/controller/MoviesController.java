package yael.project.myApi.main.controller;

import jakarta.validation.Valid;
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
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.service.MoviesService;


@RestController
@RequestMapping("/movies")
public class MoviesController {

    @Autowired
    private MoviesService moviesService;

    @PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@Valid @RequestBody Movie movie) {
        Movie movieExisting = moviesService.getMovieByTitle(movie.getTitle());
        if (movieExisting != null) {
            throw new RuntimeException("Movie already exists.");
        }
        try {
            Movie savedMovie = moviesService.addMovie(movie);
            if (savedMovie != null) {
                return ResponseEntity.ok(savedMovie);
            } else {
                throw new RuntimeException("Unable to save movie with id " + movie.getId());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateMovie/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie movieDetails) {
        try {
            return ResponseEntity.ok(moviesService.updateMovie(id, movieDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(moviesService.getMovieById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/allMovies")
    public ResponseEntity<?> getAvailableMovies() {
        try {
            return ResponseEntity.ok(moviesService.getMovies());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getMoviesByGenre(@PathVariable String genre) {
        try {
            return ResponseEntity.ok(moviesService.getMoviesByGenre(genre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("deleteMovie/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            return ResponseEntity.ok(moviesService.delete(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

