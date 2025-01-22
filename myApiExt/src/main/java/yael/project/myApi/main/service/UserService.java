package yael.project.myApi.main.service;
import org.springframework.context.annotation.Bean;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yael.project.myApi.main.dao.UserRepository;
import yael.project.myApi.main.exception.ResourceNotFoundException;
import yael.project.myApi.main.model.User;
import yael.project.myApi.main.model.UserSignupRequest;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
    }

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void signup(UserSignupRequest request) {
        String email = request.email();
        Optional<User> existingUser = repository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException(String.format("User with the email address '%s' already exists.", email));
        }

        String hashedPassword = this.passwordEncoder().encode(request.password());
        User user = new User(request.username(), email, hashedPassword, request.role());
        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user;
        try {
            user = repository.findByEmail(email).orElseThrow(() ->
                    new ChangeSetPersister.NotFoundException());
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void checkUserExistsByEmail(String email){
        Optional<User> userIsExist = repository.findByEmail(email);
        if (userIsExist.isEmpty()){
            throw new ResourceNotFoundException("This User isn't signed in.");
        }
    }
}