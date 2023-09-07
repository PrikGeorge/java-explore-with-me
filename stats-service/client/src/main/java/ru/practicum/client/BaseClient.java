package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BaseClient {

    protected RestTemplate rest;

    @Autowired
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T[]> get(String path, Class<T[]> responseType, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, responseType);
    }

    protected <T> ResponseEntity<T> post(String path, T body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, responseType);
    }

    private <T, U> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable U body, Class<T> responseType) {
        HttpEntity<U> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<T> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                statServerResponse = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException();
        }
        return prepareGatewayResponse(statServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
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
