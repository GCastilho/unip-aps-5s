import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import http.Http;
import api.Input;
import login.Login;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/app", new AppHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Server is up and running on port 8000");
	}

	static class RootHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {
			if (httpExchange.getRequestMethod().equals("GET")) {
				Http.sendHtml(httpExchange, "/index.html");
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
					if (Login.validCredentials(username, password)) {
						String sessionID = Login.makeCookie(username, password);

						Headers headers = httpExchange.getResponseHeaders();
						headers.set("Set-Cookie", String.format("%s=%s; path=/app", "sessionID", sessionID));
						headers.set("Location", "/app");

						httpExchange.sendResponseHeaders(303, -1);
					} else {
						Http.send401(httpExchange);
					}
				} catch (SQLException e) {
					System.out.println("Error: "+e.getMessage());
					e.printStackTrace();
					Http.send500(httpExchange);
				}
			}
		}
	}

	static class AppHandler implements HttpHandler {
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
				if (Login.validCookie(sessionID)) {
					if (httpExchange.getRequestMethod().equals("GET")){
						// Um acesso por GET pode ser tanto um acesso a API quanto ao site
						if (httpExchange.getRequestURI().getPath().equals("/app")) {
							// Se /app foi acessada usando uma query, é um acesso a API
							if (httpExchange.getRequestURI().getQuery() == null) {
								Http.sendHtml(httpExchange, "/app/app.html");
							} else {
								// Dá pra cacessar a API por um POST?
								Input.process(httpExchange.getRequestURI().getQuery());
								Http.send404(httpExchange);   //Temporary
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
					httpExchange.getResponseHeaders().set("Location", "/");
					httpExchange.sendResponseHeaders(303, -1);
				}
			} catch (SQLException e) {
				Http.send500(httpExchange);
			}
		}
	}
}