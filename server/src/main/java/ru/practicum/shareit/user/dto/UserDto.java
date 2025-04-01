package ru.practicum.shareit.user.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserDto {
    private String name;
    private String email;
}
