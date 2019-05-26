package http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;

import java.io.IOException;

public class HttpRootHandler implements HttpHandler {
	public void handle(HttpExchange httpExchange) throws IOException {
		if (httpExchange.getRequestMethod().equals("GET")) {
			if (httpExchange.getRequestURI().getPath().equals("/")) {//
				Http.sendHtml(httpExchange, "/index.html");
			} else {
				Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
			}
		} else if (httpExchange.getRequestMethod().equals("POST")) {
			String username = null;
			String password = null;
			{
				String query = Http.getPOST(httpExchange.getRequestBody());
				String[] array = query.split("&");
				for (String str : array) {
					String[] pair = str.split("=");
					if (pair[0].equals("username")) {
						username = pair[1];
					} else if (pair[0].equals("password")) {
						password = pair[1];
					}
				}
			}
			try {
				if (DatabaseConnection.validCredentials(username, password)) {
					String sessionID = DatabaseConnection.makeCookie(username, password);
					Headers headers = httpExchange.getResponseHeaders();
					headers.set("Set-Cookie", String.format("%s=%s; path=/app", "sessionID", sessionID));
					headers.set("Location", "/app");

					httpExchange.sendResponseHeaders(303, -1);
				} else {
					Http.send401(httpExchange);
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				e.printStackTrace();
				Http.send500(httpExchange);
			}
		}
	}
}

