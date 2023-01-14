package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPartialUpdateDto;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@PropertySource("classpath:parameter.properties")
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    @Value("${gateway.searchParameterName}")
    private String SEARCH_PARAMETER;
    private String SEARCH_PARAMETER_PATH;

    @PostConstruct
    public void init() {
        SEARCH_PARAMETER_PATH = "?" + SEARCH_PARAMETER + "={" + SEARCH_PARAMETER + "}";
    }

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> update(Long ownerId, ItemDto itemDto) {
        return put("", ownerId, itemDto);
    }

    public ResponseEntity<Object> partialUpdate(Long ownerId, Long itemId, ItemPartialUpdateDto itemPartialUpdateDto) {
        return patch("/" + itemId, ownerId, itemPartialUpdateDto);
    }

    public void deleteItem(Long ownerId, Long itemId) {
        delete("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAll(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search" + SEARCH_PARAMETER_PATH, null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}