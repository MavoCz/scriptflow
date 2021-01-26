package net.voldrich.scriptflow.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecuteRequestDto {
    private String script;

    private long created = Instant.now().getEpochSecond();

    public ScriptExecuteRequestDto(String script) {
        this.script = script;
    }

}
