package yael.project.myApi.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import yael.project.myApi.main.dao.MoviesRepository;
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MoviesServiceImpl implements MoviesService {

    @Autowired
    private MoviesRepository moviesRepository;

    @Override
    public List<Movie> getMovies() {
        List<Movie> availableMovies = moviesRepository.findAll().stream().collect(Collectors.toList());
        if (availableMovies != null) {
            return availableMovies;
        } else {
            throw new ResourceNotFoundException("No movies found");
        }
    }

    @Override
    public Movie addMovie(Movie movie) {
        Movie savedMovie = moviesRepository.save(movie);
        if (savedMovie != null) {
            return savedMovie;
        } else {
            throw new RuntimeException("Unable to save movie with id " + movie.getId());
        }
    }

    @Override
    public Movie updateMovie(Long id, Movie movieDetails) {
        Optional<Movie> movieOptional = moviesRepository.findById(id);
        if (!movieOptional.isPresent()) {
            throw new ResourceNotFoundException("Movie not found with id " + id);
        } else {
            Movie movie = movieOptional.get();
            movie.setTitle(movieDetails.getTitle());
            movie.setGenre(movieDetails.getGenre());
            movie.setRating(movieDetails.getRating());
            movie.setDuration(movieDetails.getDuration());
            movie.setReleaseYear(movie.getReleaseYear());

            Movie updatedMovie = moviesRepository.save(movie);
            return updatedMovie;
        }
    }

    @Override
    public List<Movie> getMoviesByGenre(String genre) {
        List<Movie> movies = moviesRepository.findAllByGenre(genre);
        if (movies != null) {
            return movies;
        } else {
            throw new ResourceNotFoundException("Mo movies found in genre " + genre);
        }
    }

    @Override
    public Movie getMovieById(Long id) {
        Optional<Movie> movie = moviesRepository.findById(id);
        if (movie.isPresent()) {
            return movie.get();
        } else {
            throw new ResourceNotFoundException("Movie not found with id " + id);
        }
    }

    @Override
    public void isMovieExistByTitle(String title) {
        Movie movie = moviesRepository.findByTitle(title);
        if (movie != null) {
            throw new RuntimeException("Movie already exists.");
        }
    }


    @Override
    public String delete(Long id) throws ResourceNotFoundException {
        Movie movieFromDB = moviesRepository.findById(Long.valueOf(id)).orElse(null);
        if (movieFromDB == null)
            throw new ResourceNotFoundException("Movie not found with id " + id);
        moviesRepository.delete(movieFromDB);
        return "Movie [" + id + "] deleted successfully.";
    }

}
