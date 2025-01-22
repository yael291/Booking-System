package yael.project.myApi.main.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record UserSignupRequest(
        @NotBlank(message = "Name cannot be blank")
        String username ,

        @NotBlank(message = "Role cannot be blank")
        String role ,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password) {

        public UserSignupRequest(String username, String role, String email, String password) {
                this.username = username;
                this.role = role;
                this.email = email;
                this.password = password;
        }

}
