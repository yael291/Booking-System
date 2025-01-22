package yael.project.myApi.main.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.model.UserSignupRequest;

@Data
public class BookingRequest {

    private User user;

    private Long showtimeId;

    private Integer seatNumber;

    public BookingRequest(User user, long showtimeId, int seatNumber) {
        this.user=user;
        this.showtimeId=showtimeId;
        this.seatNumber=seatNumber;
    }
}
