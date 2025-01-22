package yael.project.myApi.main.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yael.project.myApi.main.model.Showtime;

import java.time.LocalDateTime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END 
                FROM Showtime s 
                WHERE s.theater = :theater 
                AND (s.startTime < :endTime AND s.endTime > :startTime)
            """)
    boolean existsOverlappingShowtime(
            @Param("theater") String theater,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}