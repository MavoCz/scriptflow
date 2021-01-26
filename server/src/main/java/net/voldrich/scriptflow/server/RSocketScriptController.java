package net.voldrich.scriptflow.server;

import lombok.extern.slf4j.Slf4j;
import net.voldrich.graal.async.script.AsyncScriptExecutor;
import net.voldrich.graal.async.script.ScriptContext;
import net.voldrich.scriptflow.server.dto.ScriptExecuteRequestDto;
import net.voldrich.scriptflow.server.dto.ScriptExecuteResponseDto;
import net.voldrich.scriptflow.server.scriptapi.ScriptHttpClient;
import org.graalvm.polyglot.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import static net.voldrich.graal.async.script.AsyncScriptExecutor.JS_LANGUAGE_TYPE;

@Slf4j
@Controller
public class RSocketScriptController {

    public final AsyncScriptExecutor asyncScriptExecutor = new AsyncScriptExecutor.Builder().build();

    private void addContextBinding(ScriptExecuteRequestDto request, ScriptContext contextWrapper, RSocketRequester requester) {
        Value bindings = contextWrapper.getContext().getBindings(JS_LANGUAGE_TYPE);
        bindings.putMember("client", new ScriptHttpClient(contextWrapper, requester));
    }

    @MessageMapping("execute-script")
    Mono<ScriptExecuteResponseDto> requestResponse(ScriptExecuteRequestDto request, RSocketRequester requester) {
        log.info("Received request-response request: {}", request);
        return asyncScriptExecutor.executeScript(
                request.getScript(),
                contextWrapper -> addContextBinding(request, contextWrapper, requester))
                .map(ScriptExecuteResponseDto::new);
    }
}
