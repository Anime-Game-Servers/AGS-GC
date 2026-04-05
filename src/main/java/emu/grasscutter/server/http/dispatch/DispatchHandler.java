package emu.grasscutter.server.http.dispatch;

import emu.grasscutter.server.http.Router;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

/**
 * Handles misc requests related to dispatch
 */
public final class DispatchHandler implements Router {

    @Override public void applyRoutes(Javalin javalin) {
        // https://dispatchosglobal.yuanshen.com/query_security_file
        //javalin.get("/query_security_file", DispatchHandler::securityFile);
    }
}
