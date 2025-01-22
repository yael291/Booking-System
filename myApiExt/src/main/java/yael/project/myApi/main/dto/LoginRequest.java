package yael.project.myApi.main.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record LoginRequest(

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password) {

        public LoginRequest(String email, String password) {
                this.email = email;
                this.password = password;
        }

}
