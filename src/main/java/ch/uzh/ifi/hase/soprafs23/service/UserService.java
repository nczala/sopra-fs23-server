package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Session;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No user found with id %d.", id)));
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        checkIfUsernameExists(newUser.getUsername());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setCreationDate(LocalDate.now());
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private void checkIfUsernameExists(String username) {
        User userByUsername = userRepository.findByUsername(username);

        String baseErrorMessage = "The %s provided %s not unique.";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }

    public User updateUser(User userInput, long id) {
        User userById = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No user exists with id %d", id)));

        if (!isNullOrBlank(userInput.getUsername()) && !userById.getUsername().equals(userInput.getUsername())) {
            checkIfUsernameExists(userInput.getUsername());
            userById.setUsername(userInput.getUsername());
        }

        if (!(userInput.getBirthday() == null)) {
            userById.setBirthday(userInput.getBirthday());
        }

        return userRepository.save(userById);
    }

    private boolean isNullOrBlank(String input) {
        return input == null || input.equals("");
    }

    public User changeStatus(long userid, UserStatus status) {
        User user = userRepository
                .findById(userid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setStatus(status);

        return userRepository.save(user);
    }
}
