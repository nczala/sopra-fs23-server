package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
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
import java.util.Date;
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

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
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

    public User login(User loginUser) {
        User userByUserName = userRepository.findByUsername(loginUser.getUsername());
        System.out.println(loginUser.getUsername() + "  " + userByUserName.getUsername());

        if (!(userByUserName != null && isPasswordCorrect(userByUserName, loginUser.getPassword()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        userByUserName.setStatus(UserStatus.ONLINE);
        return userByUserName;
    }

    private boolean isPasswordCorrect(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User logout(User logoutUser) {
        User userByToken = userRepository.findByToken(logoutUser.getToken());
        if (userByToken == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No User for token exists.");
        }
        userByToken.setStatus(UserStatus.OFFLINE);

        userByToken = userRepository.save(userByToken);
        return userByToken;
    }

    public User updateUser(User userInput) {
        User userById = userRepository.findById(userInput.getId()).get();
        String inputUsername = userInput.getUsername();
        System.out.println(userInput.getBirthday());

        if (!authenticate(userById, userInput.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to update user with id: " + userInput.getId());
        }

        if (inputUsername.equals(userById.getUsername())) {
            System.out.println("Kept Name");
        }
        else if (inputUsername == null || inputUsername == "") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username can't be empty");
        }
        else {
            checkIfUsernameExists(inputUsername);
            userById.setUsername(inputUsername);
        }

        if (userInput.getBirthday() != null) {
            userById.setBirthday(userInput.getBirthday());
            // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username can't be empty");
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong date-format");
        }

        //checkforBirthdayFormat


        User updatedUser = userRepository.save(userById);
        return updatedUser;
    }

    private boolean authenticate(User user, String token) {
        return user.getToken().equals(token);
    }


}
