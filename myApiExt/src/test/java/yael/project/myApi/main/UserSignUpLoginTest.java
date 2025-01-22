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
import yael.project.myApi.main.dao.MoviesRepository;
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.dto.LoginRequest;
import yael.project.myApi.main.dto.LoginResponse;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.model.UserSignupRequest;
import yael.project.myApi.main.service.MoviesServiceImpl;
import yael.project.myApi.main.service.UserService;
import yael.project.myApi.main.utils.JsonUtils;
import yael.project.myApi.main.utils.JwtHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration()
class UserSignUpLoginTest extends TestBase {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

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
    void testUserSignup() throws Exception {
        //user as saved in DB(password is hashed in DB)
        User userInDB = new User("yael", "yael@gmail.com", HASHED_PASSWORD, "ADMIN");
        userInDB.setId(2L);
        //signup request as ADMIN
        UserSignupRequest userSignupRequest = new UserSignupRequest("dani", "ADMIN", "dani@gmail.com", "112233");

        //Execute user signup
        MvcResult mvcResult = mvcPerformControllerApiCall(SIGNUP_API_URL, authHeader1, userSignupRequest, statusResultMatchers.isCreated());
    }

    @Test
    void testUserSignUpAlreadyExist() throws Exception {
        //user as saved in DB(password is hashed in DB)
        User userInDB = new User("yael", "dani@gmail.com", HASHED_PASSWORD, "ADMIN");
        UserSignupRequest userSignupRequest = new UserSignupRequest("dani", "ADMIN", "dani@gmail.com", "112233");

        when(userRepositoryInterface.findByEmail(Mockito.anyString())).thenReturn(Optional.of(userInDB));

        //Execute user signup(with existing user)
        MvcResult mvcResult = mvcPerformControllerApiCall(SIGNUP_API_URL, authHeader1, userSignupRequest, statusResultMatchers.isInternalServerError());

        assertEquals(mvcResult.getResponse().getContentAsString(), "User with the email address 'dani@gmail.com' already exists.");

    }

    @Test
    void testLoginGeneratedTokenIsValid() throws Exception {
        String token;
        User userInDB = new User("dani", "dani@gmail.com", HASHED_PASSWORD, "ADMIN");
        UserSignupRequest userSignupRequest = new UserSignupRequest("dani", "ADMIN", "dani@gmail.com", "112233");
        //login process
        MvcResult mvcResultLogin = userLoginProcess(userSignupRequest, userInDB, statusResultMatchers);
        String tokenGenerated = extractTokenFromLoginResp(mvcResultLogin);
        //check token is valid
        assertTrue(tokenGenerated.startsWith("Bearer "));
        token = tokenGenerated.substring(7);
        //check token contains user's email
        String username = JwtHelper.extractUsername(token); //user's email
        assertEquals(userInDB.getEmail(), username);
        // check token contains user's role
        List<String> roles = JwtHelper.getRoles(token);
        assertEquals("ROLE_ADMIN", roles.get(0));

    }


    String extractTokenFromLoginResp(MvcResult mvcResultLogin) throws UnsupportedEncodingException {
        //extract user's token from user's login response
        LoginResponse loginResponse = JsonUtils.jsonToObject(mvcResultLogin.getResponse().getContentAsString(), LoginResponse.class);
        String tokenGenerated = "Bearer " + loginResponse.token();
        return tokenGenerated;
    }

    MvcResult userLoginProcess(UserSignupRequest userSignupRequest, User userInDB, StatusResultMatchers
            statusResultMatchers) throws Exception {
        // //user login data
        LoginRequest loginRequest = new LoginRequest(userSignupRequest.email(), "112233"); //user login data
        //prepare user's authentication+authorization data
        prepareLoginRequestData(loginRequest, userInDB);

        //Execute login
        MvcResult mvcResultLogin = mvcPerformControllerApiCall(LOGIN_API_URL, authHeader1, loginRequest, statusResultMatchers.isOk());
        return mvcResultLogin;
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


}