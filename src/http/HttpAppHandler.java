package http;

import api.Input;
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
                if (httpExchange.getRequestMethod().equals("GET")){
                    // Um acesso por GET pode ser tanto um acesso a API quanto ao site
                    if (httpExchange.getRequestURI().getPath().equals("/app")) {
                        // Se /app foi acessada usando uma query, é um acesso a API
                        if (httpExchange.getRequestURI().getQuery() == null) {
                            Http.sendHtml(httpExchange, "/app/app.html");
                        } else {
                            // Dá pra cacessar a API por um POST?
                            String response = Input.process(httpExchange.getRequestURI().getQuery());
                            Http.sendJson(httpExchange, response);
                        }
                    } else {
                        Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
                    }
                } else if (httpExchange.getRequestMethod().equals("POST")) {
                    // Acessar /app por post é um acesso a API
                    Input.process(Http.getPOST(httpExchange.getRequestBody()));
                    Http.send404(httpExchange);
                }
            } else {
                // Se /app foi acessada usando uma query, é um acesso a API e deve ser respondido com json
                if (httpExchange.getRequestURI().getQuery() == null) {
                    httpExchange.getResponseHeaders().set("Location", "/");
                    httpExchange.sendResponseHeaders(303, -1);
                } else {
                    String response = Input.process("command=notLoggedIn");
                    Http.sendJson(httpExchange, response);
                }
            }
        } catch (Exception e) {
            Http.send500(httpExchange);
        }
    }
}
