package yael.project.myApi.main.service;

import org.springframework.http.ResponseEntity;
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.Movie;

import java.util.List;

public interface MoviesService {

    List<Movie> getMovies();

    Movie addMovie(Movie movie);

    Movie updateMovie(Long id, Movie movieDetails);

    Movie getMovieById(Long id);

    Movie getMovieByTitle(String title);

    List<Movie> getMoviesByGenre(String genre);

    String delete(Long id) throws ResourceNotFoundException;
}
