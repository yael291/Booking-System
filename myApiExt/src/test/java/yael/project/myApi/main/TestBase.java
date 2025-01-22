package yael.project.myApi.main;

import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class TestBase {
    protected  static final String authHeader1 = "YaelIsDefinitelyTheBestAndSheWouldBeTheBestFitForAT&T12345678910";
    protected static final String HASHED_PASSWORD = "$2a$12$mZf2a6F8HttiFIEACyBHyOOkgy1ToR2akcUA.CdawS/mxXwdyChcS";

    protected static final String SIGNUP_API_URL = "/users/signup";
    protected static final String LOGIN_API_URL = "/users/login";

    protected static final StatusResultMatchers statusResultMatchers = MockMvcResultMatchers.status(); // to check expected statuses

    @Autowired
    protected MockMvc mvc;

}