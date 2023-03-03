package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Session;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.AuthService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("Auth-Token") String token) {
        authService.authUser(token);
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody @Valid UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User createdUser = userService.createUser(userInput);

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable("id") long id, @RequestHeader("Auth-Token") String token) {
        authService.authUser(token);

        User user = userService.getUserById(id);

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO updateUser(@PathVariable("id") long id, @RequestBody UserPutDTO userPutDTO, @RequestHeader("Auth-Token") String token) {
        authService.authUserForUserId(token, id);

        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO, id);

        User updatedUser = userService.updateUser(userInput, id);

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
    }

    @PostMapping("/session")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SessionGetDTO login(@Validated @RequestBody SessionPostDTO sessionPostDTO) {
        Session session = DTOMapper.INSTANCE.convertSessionPostDTOtoEntity(sessionPostDTO);

        Session successfulSession = authService.login(session);
        userService.changeStatus(successfulSession.getUserid(), UserStatus.ONLINE);

        return DTOMapper.INSTANCE.convertEntityToSessionGetDTO(successfulSession);
    }

    @DeleteMapping("/session")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logout(@RequestHeader("Auth-Token") String token) {
        long userid = authService.logout(token);

        userService.changeStatus(userid, UserStatus.OFFLINE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
