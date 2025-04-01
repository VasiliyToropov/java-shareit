package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    List<ItemRequestDto> toItemRequestsDto(List<ItemRequest> itemRequests);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}
