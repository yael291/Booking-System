package yael.project.myApi.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import yael.project.myApi.main.dao.MoviesRepository;
import yael.project.myApi.main.dao.ShowtimeRepository;
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.dto.LoginRequest;
import yael.project.myApi.main.dto.LoginResponse;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.model.Showtime;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.model.UserSignupRequest;
import yael.project.myApi.main.service.ShowtimeServiceImpl;
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
class ShowtimeControllerRoleAuthTest extends TestBase {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String SHOWTIMES_URL_PREFIX = "/showtimes";
    private static final String ADD_SHOWTIMES_API_URL = SHOWTIMES_URL_PREFIX + "/add";
    private static final String GET_SHOWTIME_URL_PREFIX_API_URL = SHOWTIMES_URL_PREFIX + "/get/1";
    private static final String UPDATE_SHOWTIMES_URL_PREFIX_API_URL = SHOWTIMES_URL_PREFIX + "/update/1";
    private static final String DELETE_SHOWTIME_bY_ID = SHOWTIMES_URL_PREFIX + "/delete/1";

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
    private ShowtimeServiceImpl showtimeService;
    @MockBean
    private ShowtimeRepository showtimeRepository;
    @MockBean
    private MoviesRepository moviesRepository;


    @Test
    void testAddAsAdmin_ExpectedOk() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare mock showtime data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareMockShowtimeData(showtime);
        //prepare showtime json
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonShowtime = mapper.writeValueAsString(showtime);

        //Execute add movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultAdd = mvcPerformControllerPostApiCall(ADD_SHOWTIMES_API_URL, tokenGenerated, jsonShowtime, showtime, statusResultMatchers.isOk());
        //Assert resp
        Showtime showtimeResp = mapper.readValue(mvcResultAdd.getResponse().getContentAsString(), Showtime.class);
        assertEquals(showtimeResp, showtime);
    }

    @Test
    void testGetAsAdmin_ExpectedOk() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare mock showtime data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareMockShowtimeData(showtime);
        //prepare showtime json
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonShowtime = mapper.writeValueAsString(showtime);

        //Execute get movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultGet = mvcPerformControllerGetApiCall(GET_SHOWTIME_URL_PREFIX_API_URL, tokenGenerated, jsonShowtime, showtime, statusResultMatchers.isOk());

        //Assert resp
        Showtime showtimeResp = mapper.readValue(mvcResultGet.getResponse().getContentAsString(), Showtime.class);
        assertEquals(showtimeResp, showtime);
    }

    @Test
    void testGetAsCUSTOMER_ExpectedForbidden() throws Exception {
        //extract user's login token as CUSTOMER
        String tokenGenerated = getTokenAsLoggedByRole("CUSTOMER");
        //prepare mock showtime data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareMockShowtimeData(showtime);
        //prepare showtime json
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonShowtime = mapper.writeValueAsString(showtime);

        //Execute get movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultGet = mvcPerformControllerGetApiCall(GET_SHOWTIME_URL_PREFIX_API_URL, tokenGenerated, jsonShowtime, showtime, statusResultMatchers.isForbidden());

        //Assert resp
        assertEquals("Forbidden", mvcResultGet.getResponse().getErrorMessage());
    }


    @Test
    void testUpdateAsAdmin_ExpectedOK() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare mock showtime data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        prepareMockShowtimeData(showtime);
        //prepare showtime json
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonShowtime = mapper.writeValueAsString(showtime);

        //Execute update showtime api with yael's data as a ADMIN(from token)
        MvcResult mvcResultPut = mvcPerformControllerPutApiCall(UPDATE_SHOWTIMES_URL_PREFIX_API_URL, tokenGenerated, jsonShowtime, showtime, statusResultMatchers.isOk());

        //Assert resp
        Showtime showtimeResp = mapper.readValue(mvcResultPut.getResponse().getContentAsString(), Showtime.class);
        assertEquals(showtimeResp, showtime);
    }

    @Test
    void testDeleteAsCUSTOMER_ExpectedForbidden() throws Exception {
        //extract user's login token as CUSTOMER
        String tokenGenerated = getTokenAsLoggedByRole("CUSTOMER");
        //prepare showtime data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        Showtime showtime = new Showtime(1L, movie, "movieland", LocalDateTime.parse("2025-01-18T10:11:30"), LocalDateTime.parse("2025-01-18T10:12:30"), 120.0, 5);
        //prepare showtime json
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonShowtime = mapper.writeValueAsString(showtime);

        //Execute update showtime api with yael's data as a ADMIN(from token)
        MvcResult mvcResultDelete = mvcPerformControllerPutApiCall(DELETE_SHOWTIME_bY_ID, tokenGenerated, jsonShowtime, showtime, statusResultMatchers.isForbidden());

        //Assert resp
        assertEquals("Forbidden", mvcResultDelete.getResponse().getErrorMessage());

    }

    String getTokenAsLoggedByRole(String role) throws Exception {
        //user as saved in DB(password is hashed in DB)
        User userInDB = new User("yael", "yael@gmail.com", HASHED_PASSWORD, role);
        userInDB.setId(1L);
        //user signup request as ADMIN
        UserSignupRequest userSignupRequest = new UserSignupRequest("yael", role, "yael@gmail.com", "112233");

        //Execute user Login
        MvcResult mvcResultLogin = userLoginProcess(userSignupRequest, userInDB, statusResultMatchers);

        //extract user's token from user's login response
        String tokenGenerated = extractTokenFromLoginResp(mvcResultLogin);
        return tokenGenerated;
    }

    MvcResult userLoginProcess(UserSignupRequest userSignupRequest, User userInDB, StatusResultMatchers statusResultMatchers) throws Exception {
        // //user login data
        LoginRequest loginRequest = new LoginRequest(userSignupRequest.email(), "112233"); //user login data
        //prepare user's authentication+authorization data
        prepareLoginRequestData(loginRequest, userInDB);

        //Execute login
        MvcResult mvcResultLogin = mvcPerformControllerPostApiCall(LOGIN_API_URL, authHeader1, JsonUtils.objectToJson(loginRequest), loginRequest, statusResultMatchers.isOk());
        return mvcResultLogin;
    }

    MvcResult mvcPerformControllerPostApiCall(String url, String token, String jsonObj, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObj)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }

    MvcResult mvcPerformControllerGetApiCall(String url, String token, String jsonObj, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObj)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }

    MvcResult mvcPerformControllerPutApiCall(String url, String token, String jsonObj, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .put(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObj)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }

    MvcResult mvcPerformControllerDeleteApiCall(String url, String token, String jsonObj, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObj)
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

    void prepareMockShowtimeData(Showtime showtime) {
        when(showtimeRepository.existsOverlappingShowtime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        when(showtimeRepository.save(showtime)).thenReturn(showtime);
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(moviesRepository.findById(showtime.getMovie().getId())).thenReturn(Optional.of(showtime.getMovie()));
    }

}