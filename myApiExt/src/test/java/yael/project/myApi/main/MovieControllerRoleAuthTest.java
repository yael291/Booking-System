package yael.project.myApi.main;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.dto.LoginRequest;
import yael.project.myApi.main.dto.LoginResponse;
import yael.project.myApi.main.model.Movie;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.model.UserSignupRequest;
import yael.project.myApi.main.service.MoviesServiceImpl;
import yael.project.myApi.main.service.UserService;
import yael.project.myApi.main.utils.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration()
class MovieControllerRoleAuthTest extends TestBase {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String MOVIE_URL_PREFIX = "/movies";
    private static final String ADD_MOVIES_API_URL = MOVIE_URL_PREFIX + "/addMovie";
    private static final String GET_MOVIE_API_URL = MOVIE_URL_PREFIX + "/1";
    private static final String UPDATE_MOVIE_API_URL = MOVIE_URL_PREFIX + "/updateMovie/1";

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
    private MoviesServiceImpl moviesService;
    @MockBean
    private MoviesRepository moviesRepository;


    @Test
    void testLoginAsADMINAddMovie_ExpectedOk() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare movie data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        prepareMovieData(movie);

        //Execute add movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultAdd = mvcPerformControllerPostApiCall(ADD_MOVIES_API_URL, tokenGenerated, movie, statusResultMatchers.isOk());

        //Assert resp
        ObjectMapper objectMapper = new ObjectMapper();
        Movie movieRespDTO = objectMapper.readValue(mvcResultAdd.getResponse().getContentAsString(), Movie.class);
        assertEquals(movie, movieRespDTO);
    }

    @Test
    void testLoginAsADMINGetMovie_ExpectedOk() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare movie data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        prepareMovieData(movie);

        //Execute add movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultGet = mvcPerformControllerGetApiCall(GET_MOVIE_API_URL, tokenGenerated, movie, statusResultMatchers.isOk());

        //Assert resp
        ObjectMapper objectMapper = new ObjectMapper();
        Movie movieRespDTO = objectMapper.readValue(mvcResultGet.getResponse().getContentAsString(), Movie.class);
        assertEquals(movie, movieRespDTO);
    }

    @Test
    void testLoginAsCUSTOMERGetMovie_ExpectedForbidden() throws Exception {
        //extract user's login token as CUSTOMER
        String tokenGenerated = getTokenAsLoggedByRole("CUSTOMER");
        //prepare movie data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        prepareMovieData(movie);

        //Execute add movies api with yael's data as a ADMIN(from token)
        MvcResult mvcResultGet = mvcPerformControllerGetApiCall(GET_MOVIE_API_URL, tokenGenerated, movie, statusResultMatchers.isForbidden());

        //Assert resp
        assertEquals("Forbidden", mvcResultGet.getResponse().getErrorMessage());
    }


    @Test
    void testLoginAsADMINUpdateMovie_ExpectedOK() throws Exception {
        //extract user's login token as ADMIN
        String tokenGenerated = getTokenAsLoggedByRole("ADMIN");
        //prepare movie data
        Movie movie = new Movie(1L, "Harry Potter", "Family", 4, 150.0, "1999");
        prepareMovieData(movie);

        //Execute update movie api with yael's data as a ADMIN(from token)
        MvcResult mvcResultPut = mvcPerformControllerPutApiCall(UPDATE_MOVIE_API_URL, tokenGenerated, movie, statusResultMatchers.isOk());

        //Assert resp
        ObjectMapper objectMapper = new ObjectMapper();
        Movie movieRespDTO = objectMapper.readValue(mvcResultPut.getResponse().getContentAsString(), Movie.class);
        assertEquals(movie, movieRespDTO);
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
        MvcResult mvcResultLogin = mvcPerformControllerPostApiCall(LOGIN_API_URL, authHeader1, loginRequest, statusResultMatchers.isOk());
        return mvcResultLogin;
    }

    MvcResult mvcPerformControllerPostApiCall(String url, String token, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.objectToJson(object, false))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }

    MvcResult mvcPerformControllerGetApiCall(String url, String token, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .header(AUTHORIZATION_HEADER_NAME, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.objectToJson(object, false))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(resultMatcher)
                .andReturn();
    }
    MvcResult mvcPerformControllerPutApiCall(String url, String token, Object object, ResultMatcher resultMatcher) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .put(url)
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

    void prepareMovieData(Movie movie) {
        when(moviesRepository.save(movie)).thenReturn(movie);
        when(moviesRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
    }

}