package net.voldrich.scriptflow.client;

import lombok.extern.slf4j.Slf4j;
import net.voldrich.scriptflow.client.dto.HttpRequestDto;
import net.voldrich.scriptflow.client.dto.HttpResponseDto;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class ClientHandler {

    private final WebClient client;

    public ClientHandler(WebClient client) {
        this.client = client;
    }

    @MessageMapping("http-request")
    public Mono<HttpResponseDto> performHttpRequest(HttpRequestDto message) {
        log.info("Received message from server {}", message);

        WebClient.RequestBodySpec requestBodySpec = client.method(HttpMethod.valueOf(message.getMethod())).uri(message.getUri());

        if (message.getHeaders() != null) {
            message.getHeaders().forEach(requestBodySpec::header);
        }


        if (message.getBody() != null) {
            requestBodySpec.bodyValue(message.getBody());
        }

        return requestBodySpec.exchangeToMono(this::handleResponse);
    }

    private Mono<HttpResponseDto> handleResponse(ClientResponse response) {
        log.info("Request finished with status {}", response.statusCode());
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class)
                    .map(body -> new HttpResponseDto(response, body));
        } else if (response.statusCode().is4xxClientError()) {
            return response.createException().flatMap(Mono::error);
        } else {
            return response.createException().flatMap(Mono::error);
        }
    }
}
