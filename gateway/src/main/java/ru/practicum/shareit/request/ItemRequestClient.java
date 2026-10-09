package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {

	private static final String API_PREFIX = "/requests";

	private static final String BY_ID = "/%d";

	private static final String ALL = "/all";

	@Autowired
	public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(BaseClient::createRequestFactory)
						.build()
		);
	}

	public ResponseEntity<ItemRequestDto> createRequest(long userId, ItemRequestDto requestDto) {
		return post("", userId, requestDto, ItemRequestDto.class);
	}

	public ResponseEntity<ItemRequestDto[]> getRequests(long userId) {
		return get("", userId, ItemRequestDto[].class);
	}

	public ResponseEntity<ItemRequestDto[]> getAllRequests(long userId) {
		return get(ALL, userId, ItemRequestDto[].class);
	}

	public ResponseEntity<ItemRequestDto> getRequest(long userId, long requestId) {
		return get(String.format(BY_ID, requestId), userId, ItemRequestDto.class);
	}
}
