package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@PropertySource("classpath:parameter.properties")
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    @Value("${gateway.fromParameterName}")
    private String FROM_PARAMETER;
    @Value("${gateway.sizeParameterName}")
    private String SIZE_PARAMETER;
    private String PAGINATION_PATH;

    @PostConstruct
    public void init() {
        PAGINATION_PATH = "?" + FROM_PARAMETER + "={"
                + FROM_PARAMETER + "}&" + SIZE_PARAMETER + "={" + SIZE_PARAMETER + "}";
    }

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long requestorId, ItemRequestDto itemDto) {
        return post("", requestorId, itemDto);
    }

    public ResponseEntity<Object> getAllByRequestor(Long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                FROM_PARAMETER, from,
                SIZE_PARAMETER, size
        );
        return get("/all" + PAGINATION_PATH, userId, parameters);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}