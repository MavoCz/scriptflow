package net.voldrich.scriptflow.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequestDto {
    private String method;
    private String uri;
    private Map<String, String> headers;
    private String body;
}
