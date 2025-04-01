package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;
import java.util.Optional;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemDto item, Optional<Long> optionalUserId) {
        Long userId = optionalUserId.orElseThrow(() -> new BadRequestException("Неверный запрос"));

        return post("", userId, item);
    }

    public ResponseEntity<Object> addComment(CommentDto comment, Optional<Long> optionalUserId, Long itemId) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return post("/" + itemId + "/comment", userId, comment);
    }

    public ResponseEntity<Object> updateItem(ItemDto item, Optional<Long> optionalUserId, Long itemId) {
        Long userId = optionalUserId.orElseThrow(() -> new BadRequestException("Неверный запрос"));

        return patch("/" + itemId, userId, item);
    }

    //itemDtoWithTime
    public ResponseEntity<Object> getItemWithTimeAndComments(Optional<Long> optionalUserId, Long itemId) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(Optional<Long> optionalUserId) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(Optional<Long> optionalUserId, String text) {
        Long userId = optionalUserId.orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Map<String, Object> parameters = Map.of(
                "text", text
        );

        return get("/search?text={text}", userId, parameters);
    }
}
