package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Vasiliy Toropov");
        userDto.setEmail("toropovforwork@mail.ru");

        user = new User();
        user.setId(1L);
        user.setName("Vasiliy Toropov");
        user.setEmail("toropovforwork@mail.ru");
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vasiliy Toropov"))
                .andExpect(jsonPath("$.email").value("toropovforwork@mail.ru"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.getUsersDto()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Vasiliy Toropov"))
                .andExpect(jsonPath("$[0].email").value("toropovforwork@mail.ru"));

        verify(userService, times(1)).getUsersDto();
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vasiliy Toropov"))
                .andExpect(jsonPath("$.email").value("toropovforwork@mail.ru"));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(UserDto.class), eq(1L))).thenReturn(user);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vasiliy Toropov"))
                .andExpect(jsonPath("$.email").value("toropovforwork@mail.ru"));

        verify(userService, times(1)).updateUser(any(UserDto.class), eq(1L));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}
