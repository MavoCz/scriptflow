package net.voldrich.scriptflow.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponseDto {
    private int status;
    private Map<String, String> headers;
    private String body;

    public HttpResponseDto(ClientResponse response, String body) {
        this.status = response.rawStatusCode();
        this.headers = response.headers().asHttpHeaders().toSingleValueMap();
        this.body = body;
    }
}
