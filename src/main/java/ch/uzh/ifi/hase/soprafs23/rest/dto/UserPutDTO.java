package ch.uzh.ifi.hase.soprafs23.rest.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class UserPutDTO {
    @NotBlank
    private Long id;
    private String username;
    private LocalDate birthday;
    @NotBlank
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
