package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;

    private List<ItemDto> items = new ArrayList<>();
}
