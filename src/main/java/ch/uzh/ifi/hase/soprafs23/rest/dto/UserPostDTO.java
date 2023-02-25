package ch.uzh.ifi.hase.soprafs23.rest.dto;


import javax.validation.constraints.NotBlank;

public class UserPostDTO {

    @NotBlank(message = "The username must not be empty.")
    private String username;

    @NotBlank(message = "The password must not be empty.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
