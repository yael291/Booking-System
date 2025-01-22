package yael.project.myApi.main.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yael.project.myApi.main.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Seat s WHERE s.seatNumber = :seatNumber AND s.showtime.id = :showtimeId AND s.isBooked = true")
    boolean isSeatBooked(@Param("seatNumber") Integer seatNumber, @Param("showtimeId") Long showtimeId);

    @Transactional // Required for executing update queries
    @Modifying // Indicates that this query modifies data
    @Query("UPDATE Seat s SET s.isBooked = true WHERE s.seatNumber = :seatNumber AND s.showtime.id = :showtimeId")
    void markSeatAsBooked(@Param("seatNumber") Integer seatNumber, @Param("showtimeId") Long showtimeId);

}
