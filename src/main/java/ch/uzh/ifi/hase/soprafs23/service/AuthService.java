package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Session;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void authUserForUserId(String inputToken, long userid) {
        String token = userRepository
                .findById(userid)
                .map(user -> user.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid user id %s", userid)));

        if (!token.equals(inputToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public void authUser(String token) {
        Set<String> allTokens = userRepository
                .findAll()
                .stream()
                .map(user -> user.getToken())
                .collect(Collectors.toSet());

        if (!allTokens.contains(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public Session login(Session session) {
        User userByUserName = userRepository.findByUsername(session.getUsername());

        if (!(userByUserName != null && isPasswordCorrect(userByUserName, session.getPassword()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return new Session(
                userByUserName.getUsername(),
                null,
                userByUserName.getId(),
                userByUserName.getToken()
        );
    }

    private boolean isPasswordCorrect(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public long logout(String token) {
        return userRepository
                .findByToken(token)
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
