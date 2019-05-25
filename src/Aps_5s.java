import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import api.ServerSocketHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import http.Http;
import api.Input;
import database.DatabaseConnection;

public class Aps_5s {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/app", new AppHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("HTTP server is up and running on port 8000");

		Server api_server = new Server(8080);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(api.ServerSocketHandler.class);
			}
		};
		api_server.setHandler(wsHandler);
		api_server.start();
		//api_server.join();
		System.out.println("API server is up and running on port 8080");
	}

	static class RootHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {
			if (httpExchange.getRequestMethod().equals("GET")) {
				if (httpExchange.getRequestURI().getPath().equals("/")) {   // Temp solution
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
}