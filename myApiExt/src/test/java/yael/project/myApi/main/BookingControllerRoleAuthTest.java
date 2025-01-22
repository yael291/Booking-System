package yael.project.myApi.main;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import yael.project.myApi.main.dao.BookingRepository;
import yael.project.myApi.main.dao.SeatRepository;
import yael.project.myApi.main.dao.ShowtimeRepository;
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.dto.BookingRequest;
import yael.project.myApi.main.dto.LoginRequest;
import yael.project.myApi.main.dto.LoginResponse;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.model.Showtime;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.service.BookingServiceImpl;
import yael.project.myApi.main.service.UserService;
import yael.project.myApi.main.utils.JsonUtils;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration()
class BookingControllerRoleAuthTest extends TestBase {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BOOKING_API_URL = "/bookings/book";

    @Autowired
    @InjectMocks
    private UserService userService;
    @MockBean
    UserRepository userRepositoryInterface;
    @Mock
    private Authentication authentication;
    @Spy
    private AuthenticationManager authenticationManager;
    @Autowired
    @InjectMocks
    private BookingServiceImpl bookingService;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ShowtimeRepository showtimeRepository;
    @MockBean
    private SeatRepository seatRepository;

    @Test
    void testSignupLoginAsCustomerCreateBooking_Expected_BookingOk() throws Exception {
        //user as saved in DB(password is hashed in DB)
        User userInDB = new User("dani", "dani@gmail.com", HASHED_PASSWORD, "CUSTOMER");

        //Execute user Login
        MvcResult mvcResultLogin = userLoginProcess(userInDB, statusResultMatchers);

        //extract user's token from user's login response
        String tokenGenerated = extractTokenFromLoginResp(mvcResultLogin);
        //prepare booking data
        BookingRequest bookingRequest = new BookingRequest(userInDB, 1L, 3);
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareBookingData(bookingRequest, showtime);

        //Execute booking api with dani's data as a CUSTOMER(from token)
        MvcResult mvcResultBooking = mvcPerformControllerApiCall(BOOKING_API_URL, tokenGenerated, bookingRequest, statusResultMatchers.isOk());

        //Assert
        String bookingRes = mvcResultBooking.getResponse().getContentAsString();
        assertEquals("Ticket booked successfully!", bookingRes);
    }

    @Test
    void testSignupLoginAsAdminCreateBooking_Expected_BookingForbidden() throws Exception {
        //user as saved in DB(password is hashed in DB)
        User userInDB = new User("yael", "yael@gmail.com", HASHED_PASSWORD, "ADMIN");

        //Execute user Login
        MvcResult mvcResultLogin = userLoginProcess(userInDB, statusResultMatchers);

        //extract user's token from user's login response
        String tokenGenerated = extractTokenFromLoginResp(mvcResultLogin);
        //prepare booking data
        BookingRequest bookingRequest = new BookingRequest(userInDB, 1L, 3);
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareBookingData(bookingRequest, showtime);

        //Execute booking api with dani's data as a CUSTOMER(from token)
        MvcResult mvcResultBooking = mvcPerformControllerApiCall(BOOKING_API_URL, tokenGenerated, bookingRequest, statusResultMatchers.isForbidden());

        //Assert
        String bookingRes = mvcResultBooking.getResponse().getErrorMessage();
        assertEquals("Forbidden", bookingRes);
    }

    MvcResult userLoginProcess(User userInDB, StatusResultMatchers statusResultMatchers) throws Exception {
        // //user login data
        LoginRequest loginRequest = new LoginRequest(userInDB.getEmail(), "112233"); //user login data
        //prepare user's authentication+authorization data
        prepareLoginRequestData(loginRequest, userInDB);

        //Execute login
        MvcResult mvcResultLogin = mvcPerformControllerApiCall(LOGIN_API_URL, authHeader1, loginRequest, statusResultMatchers.isOk());
        return mvcResultLogin;
    }

    MvcResult mvcPerformControllerApiCall(String url, String token, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.objectToJson(object, false))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }

    String extractTokenFromLoginResp(MvcResult mvcResultLogin) throws UnsupportedEncodingException {
        //extract user's token from user's login response
        LoginResponse loginResponse = JsonUtils.jsonToObject(mvcResultLogin.getResponse().getContentAsString(), LoginResponse.class);
        String tokenGenerated = "Bearer " + loginResponse.token();
        return tokenGenerated;
    }

    void prepareLoginRequestData(LoginRequest loginRequest, User userInDB) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(userRepositoryInterface.findByEmail(loginRequest.email())).thenReturn(Optional.of(userInDB));
        try (MockedStatic<BCrypt> BCcryptStatic = Mockito.mockStatic(BCrypt.class)) {
            BCcryptStatic.when(() -> BCrypt.checkpw("112233", HASHED_PASSWORD))
                    .thenReturn(true);
        }
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
    }

    void prepareBookingData(BookingRequest bookingRequest, Showtime showtime) {
        when(showtimeRepository.findById(bookingRequest.getShowtimeId())).thenReturn(Optional.of(showtime));
        //seat is not booked
        when(seatRepository.isSeatBooked(bookingRequest.getSeatNumber(), bookingRequest.getShowtimeId())).thenReturn(false);
        when(showtimeRepository.getOne(showtime.getId())).thenReturn(showtime);
    }

}