package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.AuthService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;


    @Test
    public void givenAuthUser_andUser_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        String token = "test-token";

        User user = new User();
        user.setUsername("testUsername");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", token);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
        Mockito.verify(authService).authUser(token);
    }

    @Test
    public void givenNotAuthUser_andUser_whenGetUsers_thenThrowUNAUTHORIZED() throws Exception {
        // given
        String token = "testToken";

        // this mocks the AuthService -> throws 401 when called
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(authService).authUser(token);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", token);

        // then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }


    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("testToken");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_invalidInput_throwCONFLICT() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");

        given(userService.createUser(Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void givenAuthUser_getUserById_userFound_thenReturnJsonArray() throws Exception {
        //given
        long userid = 1;
        String token = "testToken";

        User user = new User();
        user.setId(userid);
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);

        Mockito.when(userService.getUserById(userid)).thenReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/users/" + userid)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", token);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));

        Mockito.verify(authService).authUser(token);
    }

    @Test
    public void givenNotAuthUser_getUserById_thenThrowNOTFOUND() throws Exception {
        //given
        long userid = 1;
        String token = "testToken";

        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
                .when(authService).authUser(token);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/users/" + userid)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", token);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAuthUser_getUserById_userNotFound_thenThrowNOTFOUND() throws Exception {
        //given
        long userid = 1;
        String token = "testToken";

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).getUserById(userid);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/users/" + userid)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", token);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());

        Mockito.verify(authService).authUser(token);
    }

    @Test
    public void givenAuthUserForID_updateUser_validInput_userUpdated() throws Exception {
        // given
        long userid = 1;
        String token = "testToken";
        LocalDate date = LocalDate.now();

        User user = new User();
        user.setId(userid);
        user.setUsername("testUsername");
        user.setToken("testToken");
        user.setBirthday(date);
        user.setStatus(UserStatus.ONLINE);

        JSONObject obj = new JSONObject();
        obj.put("username", "testUsername");
        obj.put("birthday", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        given(userService.updateUser(Mockito.any(), Mockito.anyLong())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/" + userid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obj))
                .header("Auth-Token", token);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));

        Mockito.verify(authService).authUserForUserId(token, userid);
    }

    @Test
    public void givenAuthUserForID_updateUser_invalidInput_throwNOTFOUND() throws Exception {
        // given
        long userid = 1;
        String token = "testToken";
        LocalDate date = LocalDate.now();

        JSONObject obj = new JSONObject();
        obj.put("username", "testUsername");
        obj.put("birthday", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        given(userService.updateUser(Mockito.any(), Mockito.anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/users/" + userid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obj))
                .header("Auth-Token", token);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}