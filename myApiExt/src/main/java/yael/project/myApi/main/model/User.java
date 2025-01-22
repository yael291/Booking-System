package yael.project.myApi.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @NotNull
    private  String email;

    @NotNull
    private  String password;
    @NotNull
    private String role; //can be ADMIN or CUSTOMER


    public User(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public static class Builder {
        private String email;
        private String password;
        private String role;
        private String name;

        public static Builder newInstance()
        {
            return new Builder();
        }

        private Builder(){}

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public User build()
        {
            return new User(this);
        }
    }
}

