package net.voldrich.scriptflow.server.scriptapi;

import net.voldrich.graal.async.script.ScriptContext;
import net.voldrich.graal.async.script.ScriptUtils;
import net.voldrich.scriptflow.server.dto.HttpRequestDto;
import net.voldrich.scriptflow.server.dto.HttpResponseDto;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.TypeLiteral;
import org.graalvm.polyglot.Value;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ScriptHttpClient {
    public static final TypeLiteral<Map<String, String>> STRING_MAP = new TypeLiteral<>() {
    };


    private final ScriptContext scriptContext;

    private final RSocketRequester requester;

    public ScriptHttpClient(ScriptContext contextWrapper, RSocketRequester requester) {
        this.scriptContext = contextWrapper;
        this.requester = requester;
    }

    @HostAccess.Export
    public Value get(String url) {
        return performHttpOperation(HttpMethod.GET, url, null, null);
    }

    @HostAccess.Export
    public Value get(String url, Value config) {
        return performHttpOperation(HttpMethod.GET, url, null, config);
    }

    @HostAccess.Export
    public Value post(String url, Value data) {
        return performHttpOperation(HttpMethod.POST, url, data, null);
    }

    @HostAccess.Export
    public Value post(String url, Value data, Value config) {
        return performHttpOperation(HttpMethod.POST, url, data, config);
    }

    private Value performHttpOperation(HttpMethod httpMethod, String url, Value data, Value config) {

        HttpRequestDto requestDto = new HttpRequestDto();

        requestDto.setUri(url);
        requestDto.setMethod(httpMethod.name());

        if (config != null) {
            Value headers = config.getMember("headers");
            if (headers != null) {
                requestDto.setHeaders(headers.as(STRING_MAP));
            }
        }

        if (data != null) {
            if (data.isString()) {
                requestDto.setBody(data.toString());
            } else {
                requestDto.setBody(ScriptUtils.stringify(scriptContext.getContext(), data).toString());
            }
        }

        Mono<ScriptHttpResponse> operation = requester.route("http-request")
                .data(requestDto)
                .retrieveMono(HttpResponseDto.class)
                .map(responseDto -> new ScriptHttpResponse(scriptContext.getContext(), responseDto));

        return scriptContext.executeAsPromise(operation, "HTTP " + httpMethod.name());

    }
}
