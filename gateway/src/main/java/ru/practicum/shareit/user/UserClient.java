package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

	private static final String API_PREFIX = "/users";

	private static final String BY_ID = "/%d";

	@Autowired
	public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(BaseClient::createRequestFactory)
						.build()
		);
	}

	public ResponseEntity<UserDto> createUser(UserDto userDto) {
		return post("", userDto, UserDto.class);
	}

	public ResponseEntity<UserDto> updateUser(long userId, UserDto userDto) {
		return patch(String.format(BY_ID, userId), userDto, UserDto.class);
	}

	public ResponseEntity<UserDto> getUser(long userId) {
		return get(String.format(BY_ID, userId), UserDto.class);
	}

	public ResponseEntity<UserDto[]> getUsers() {
		return get("", UserDto[].class);
	}

	public ResponseEntity<Void> deleteUser(long userId) {
		return delete(String.format(BY_ID, userId), Void.class);
	}
}
