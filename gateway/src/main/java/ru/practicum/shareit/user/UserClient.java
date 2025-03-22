package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getUser(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getUsersDto() {
        return get("");
    }

    public ResponseEntity<Object> createUser(UserDto user) {
        return post("", user);
    }

    public ResponseEntity<Object> updateUser(UserDto user, Long id) {
        return patch("/" + id, user);
    }

    public ResponseEntity<Void> deleteUser(Long id) {
        delete("/" + id);
        return ResponseEntity.ok().build();
    }
}
