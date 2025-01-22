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
        moviesService.isMovieExistByTitle(movie.getTitle());
        return ResponseEntity.ok(moviesService.addMovie(movie));
    }

    @PutMapping("/updateMovie/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie movieDetails) {
        return ResponseEntity.ok(moviesService.updateMovie(id, movieDetails));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(moviesService.getMovieById(id));
    }


    @GetMapping("/allMovies")
    public ResponseEntity<?> getAvailableMovies() {
        return ResponseEntity.ok(moviesService.getMovies());
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(moviesService.getMoviesByGenre(genre));
    }


    @DeleteMapping("deleteMovie/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(moviesService.delete(id));
    }

}

