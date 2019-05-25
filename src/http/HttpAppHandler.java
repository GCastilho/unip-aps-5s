package http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;

import java.io.IOException;
import java.util.List;

public class HttpAppHandler implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {
        // Get sessionID cookies
        String sessionID = null;
        {
            Headers header = httpExchange.getRequestHeaders();
            List<String> cookies = header.get("Cookie");
            if (cookies != null) {
                for (String cookieString : cookies) {
                    String[] tokens = cookieString.split("\\s*;\\s*");
                    for (String token : tokens) {
                        if (token.startsWith("sessionID") && token.charAt("sessionID".length()) == '=') {
                            sessionID = token.substring("sessionID".length() + 1);
                        }
                    }
                }
            } else {
                httpExchange.getResponseHeaders().set("Location", "/");
                httpExchange.sendResponseHeaders(303, -1);
                return;
            }
        }
        try {
            if (DatabaseConnection.validCookie(sessionID)) {
                if (httpExchange.getRequestMethod().equals("GET")) {
                    if (httpExchange.getRequestURI().getPath().equals("/app")) {
                        if (httpExchange.getRequestURI().getQuery() == null) {
                            Http.sendHtml(httpExchange, "/app/app.html");
                        }
                    } else {
                        Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
                    }
                }
            } else {
                httpExchange.getResponseHeaders().set("Location", "/");
                httpExchange.sendResponseHeaders(303, -1);
            }
        } catch (Exception e) {
            Http.send500(httpExchange);
        }
    }
}
