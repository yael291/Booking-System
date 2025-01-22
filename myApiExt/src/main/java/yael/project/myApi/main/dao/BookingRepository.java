package yael.project.myApi.main.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import yael.project.myApi.main.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
