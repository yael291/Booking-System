package yael.project.myApi.main.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yael.project.myApi.main.model.Movie;

import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Long> {
    List<Movie>findAllByGenre(String genre);
    Movie findByTitle(String title);
}