package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Vasiliy Toropov");
        user.setEmail("toropovforwork@mail.ru");

        userDto = new UserDto();
        userDto.setName("Vasiliy Toropov");
        userDto.setEmail("toropovforwork@mail.ru");
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserWhenUserDoesNotExistTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUsersDtoTest() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.getUsersDto();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUserTest() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserWhenNameOrEmailIsNullTest() {
        userDto.setName(null);

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(userDto, 1L);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUserTest() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
