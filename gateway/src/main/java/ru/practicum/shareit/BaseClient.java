package ru.practicum.shareit;

import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.utils.http.RequestHeaders;

public class BaseClient {

	protected final RestTemplate rest;

	public BaseClient(RestTemplate rest) {
		this.rest = rest;
	}

	public static HttpComponentsClientHttpRequestFactory createRequestFactory() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(Timeout.ofSeconds(5))
				.setConnectionRequestTimeout(Timeout.ofSeconds(5))
				.setResponseTimeout(Timeout.ofSeconds(5))
				.build();
		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.setDefaultRequestConfig(config)
				.build();
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	protected <T> ResponseEntity<T> get(String path, Class<T> responseType) {
		return get(path, null, null, responseType);
	}

	protected <T> ResponseEntity<T> get(String path, long userId, Class<T> responseType) {
		return get(path, userId, null, responseType);
	}

	protected <T> ResponseEntity<T> get(String path, Long userId, @Nullable Map<String, Object> parameters,
										Class<T> responseType) {
		return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, responseType);
	}

	protected <T> ResponseEntity<T> post(String path, Object body, Class<T> responseType) {
		return post(path, null, null, body, responseType);
	}

	protected <T> ResponseEntity<T> post(String path, long userId, Object body, Class<T> responseType) {
		return post(path, userId, null, body, responseType);
	}

	protected <T> ResponseEntity<T> post(String path, Long userId, @Nullable Map<String, Object> parameters,
										 Object body, Class<T> responseType) {
		return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, responseType);
	}

	protected <T> ResponseEntity<T> put(String path, long userId, Object body, Class<T> responseType) {
		return put(path, userId, null, body, responseType);
	}

	protected <T> ResponseEntity<T> put(String path, long userId, @Nullable Map<String, Object> parameters,
										Object body, Class<T> responseType) {
		return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body, responseType);
	}

	protected <T> ResponseEntity<T> patch(String path, Object body, Class<T> responseType) {
		return patch(path, null, null, body, responseType);
	}

	protected <T> ResponseEntity<T> patch(String path, long userId, Class<T> responseType) {
		return patch(path, userId, null, null, responseType);
	}

	protected <T> ResponseEntity<T> patch(String path, long userId, Object body, Class<T> responseType) {
		return patch(path, userId, null, body, responseType);
	}

	protected <T> ResponseEntity<T> patch(String path, Long userId, @Nullable Map<String, Object> parameters,
										  Object body, Class<T> responseType) {
		return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
	}

	protected <T> ResponseEntity<T> delete(String path, Class<T> responseType) {
		return delete(path, null, null, responseType);
	}

	protected <T> ResponseEntity<T> delete(String path, long userId, Class<T> responseType) {
		return delete(path, userId, null, responseType);
	}

	protected <T> ResponseEntity<T> delete(String path, Long userId, @Nullable Map<String, Object> parameters,
										   Class<T> responseType) {
		return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, responseType);
	}

	private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path, Long userId,
													 @Nullable Map<String, Object> parameters, @Nullable Object body,
													 Class<T> responseType) {
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

		try {
			if (parameters != null) {
				return prepareGatewayResponse(rest.exchange(path, method, requestEntity, responseType, parameters));
			}
			return prepareGatewayResponse(rest.exchange(path, method, requestEntity, responseType));
		} catch (HttpStatusCodeException exception) {
			return errorResponse(exception);
		}
	}

	private HttpHeaders defaultHeaders(Long userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		if (userId != null) {
			headers.set(RequestHeaders.USER_ID, String.valueOf(userId));
		}
		return headers;
	}

	@SuppressWarnings("unchecked")
	private static <T> ResponseEntity<T> errorResponse(HttpStatusCodeException exception) {
		return (ResponseEntity<T>) ResponseEntity.status(exception.getStatusCode())
				.contentType(MediaType.APPLICATION_JSON)
				.body(exception.getResponseBodyAsByteArray());
	}

	private static <T> ResponseEntity<T> prepareGatewayResponse(ResponseEntity<T> response) {
		if (response.getStatusCode().is2xxSuccessful()) {
			return response;
		}

		ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

		if (response.hasBody()) {
			return responseBuilder.body(response.getBody());
		}

		return responseBuilder.build();
	}
}
