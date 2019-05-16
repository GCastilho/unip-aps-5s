package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HttpErrors {
	private static void send(HttpExchange httpExchange, String response) {
		try {
			httpExchange.sendResponseHeaders(401, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getCause());
			e.printStackTrace();
		}
	}

	public static void send401(HttpExchange httpExchange) {
		String response = "401 Unauthorized";
		send(httpExchange, response);
	}

	public static void send404(HttpExchange httpExchange) {
		String response = "404 Not Found";
		send(httpExchange, response);
	}

	public static void send500(HttpExchange httpExchange) {
		String response = "500 Internal Server Error";
		send(httpExchange, response);
	}
}
