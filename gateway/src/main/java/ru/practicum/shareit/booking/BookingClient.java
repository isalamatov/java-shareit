package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@PropertySource("classpath:parameter.properties")
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    @Value("${gateway.stateParameterName}")
    private String STATE_PARAMETER;
    @Value("${gateway.fromParameterName}")
    private String FROM_PARAMETER;
    @Value("${gateway.sizeParameterName}")
    private String SIZE_PARAMETER;
    @Value("${gateway.approvedParameterName}")
    private String APPROVE_PARAMETER;
    private String PAGINATION_PATH;

    @PostConstruct
    public void init() {
        PAGINATION_PATH = "?" + STATE_PARAMETER + "={" + STATE_PARAMETER + "}&" + FROM_PARAMETER + "={"
                + FROM_PARAMETER + "}&" + SIZE_PARAMETER + "={" + SIZE_PARAMETER + "}";
    }

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                STATE_PARAMETER, state.name(),
                FROM_PARAMETER, from,
                SIZE_PARAMETER, size
        );
        return get(PAGINATION_PATH, userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(APPROVE_PARAMETER, approved);
        String path = "?" + APPROVE_PARAMETER + "={" + APPROVE_PARAMETER + "}";
        return patch("/" + bookingId + path, ownerId, parameters, null);
    }

    public ResponseEntity<Object> getAllByOwnerAndState(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                STATE_PARAMETER, state.name(),
                FROM_PARAMETER, from,
                SIZE_PARAMETER, size
        );
        return get("/owner" + PAGINATION_PATH, userId, parameters);
    }
}