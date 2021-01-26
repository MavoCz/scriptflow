package net.voldrich.scriptflow.server.scriptapi;

import net.voldrich.graal.async.script.ScriptUtils;
import net.voldrich.scriptflow.server.dto.HttpResponseDto;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

public class ScriptHttpResponse {

    private final Context context;

    @HostAccess.Export
    public final int status;
    @HostAccess.Export
    public final String data;

    public ScriptHttpResponse(Context context, HttpResponseDto responseDto) {
        this.context = context;
        this.status = responseDto.getStatus();
        this.data = responseDto.getBody();
    }

    @HostAccess.Export
    public Value json() {
        return ScriptUtils.parseJson(context, data);
    }

    public String text() {
        return data;
    }
}

