package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        return post("", userId, itemDtoRequest);
    }

    public ResponseEntity<Object> createComment(Long itemId, CommentDtoRequest commentDtoRequest, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDtoRequest);
    }

    public ResponseEntity<Object> getItemById(Long id, Long userId) {
        return get("/" + id, userId);

    }

    public ResponseEntity<Object> getAllItemsByOwnerId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemDtoRequest itemDtoRequest, Long ownerId) {
        return patch("/" + id, ownerId, itemDtoRequest);
    }

    public ResponseEntity<Object> getAvailableItemsByText(String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", null, params);
    }
}
