package net.voldrich.scriptflow.client;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import net.voldrich.scriptflow.client.dto.ScriptExecuteRequestDto;
import net.voldrich.scriptflow.client.dto.ScriptExecuteResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@ShellComponent
public class RSocketClient {

    public static final String SERVER_URL = "ws://localhost:8080/rsocket";

    private static final WebClient client = WebClient.builder().build();

    // Add a global class variable for the RSocketRequester
    private final RSocketRequester rsocketRequester;

    private static final String SCRIPT = "(async function fetch() {\n" +
            "    const company = await client.get('https://api.coincap.io/v2/assets');\n" +
            "    console.log(company.status);\n" +
            "    const ceoList = await client.get('https://api.coincap.io/v2/assets/bitcoin');\n" +
            "    console.log(ceoList.status);\n" +
            "\n" +
            "    return {\n" +
            "        company: company.json(),\n" +
            "        ceos: ceoList.json()\n" +
            "    }\n" +
            "})";

    // Use an Autowired constructor to customize the RSocketRequester and store a reference to it in the global variable
    @Autowired
    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder, RSocketStrategies strategies) {
        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler(client));

        this.rsocketRequester = rsocketRequesterBuilder
                .rsocketConnector(connector -> connector.acceptor(responder))
                .websocket(URI.create(SERVER_URL));
    }

    @ShellMethod("Send script executor request")
    public void exec(String scriptPath) throws InterruptedException, IOException {
        log.info("\nSending one request. Waiting for one response...");
        String script = Files.readString(Path.of(scriptPath));
        ScriptExecuteResponseDto responseDto = this.rsocketRequester
                .route("execute-script")
                .data(new ScriptExecuteRequestDto(script))
                .retrieveMono(ScriptExecuteResponseDto.class)
                .block();
        log.info("Response was: {}", responseDto.getData());
    }
}
