package http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;

import java.io.IOException;

public class HttpLibsHandler implements HttpHandler {
	public void handle(HttpExchange httpExchange) throws IOException {
		Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
	}
}

