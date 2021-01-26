package net.voldrich.scriptflow.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponseDto {
    private int status;
    private Map<String, String> headers;
    private String body;
}
