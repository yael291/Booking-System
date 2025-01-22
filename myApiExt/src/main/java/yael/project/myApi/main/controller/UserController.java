package yael.project.myApi.main.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.utils.JwtHelper;
import yael.project.myApi.main.dto.LoginRequest;
import yael.project.myApi.main.dto.LoginResponse;
import yael.project.myApi.main.model.UserSignupRequest;
import yael.project.myApi.main.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupRequest requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        userService.checkUserExistsByEmail(request.email());
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String token = JwtHelper.generateToken(request.email(), request.password(), auth.getAuthorities());
        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }


}
