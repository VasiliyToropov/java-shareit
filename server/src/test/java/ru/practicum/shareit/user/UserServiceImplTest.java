package ru.practicum.shareit.user;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUsersDto_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsersDto();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUsersDto_ShouldReturnListOfUserDto_WhenUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.getUsersDto();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUser_ShouldReturnUser_WhenUserDtoIsValid() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowValidationException_WhenNameIsNull() {
        userDto.setName(null);

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowValidationException_WhenEmailIsNull() {
        userDto.setEmail(null);

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowConflictException_WhenEmailAlreadyExists() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateName_WhenOnlyNameIsProvided() {
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(updatedUserDto, 1L);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenOnlyEmailIsProvided() {
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("jane.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(updatedUserDto, 1L);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateNameAndEmail_WhenBothAreProvided() {
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Jane Doe");
        updatedUserDto.setEmail("jane.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(updatedUserDto, 1L);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void checkConflicts_ShouldThrowConflictException_WhenEmailExists() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        assertThrows(ConflictException.class, () -> userService.checkConflicts(userDto));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void checkConflicts_ShouldNotThrowException_WhenEmailDoesNotExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> userService.checkConflicts(userDto));
        verify(userRepository, times(1)).findAll();
    }
}