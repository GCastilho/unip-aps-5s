import java.io.*;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import http.HttpErrors;
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
		public void handle(HttpExchange t) throws IOException {
			if (t.getRequestMethod().equals("GET")) {
				sendHtmlFile(t, "/index.html");
			} else if (t.getRequestMethod().equals("POST")) {
				String username = null;
				String password = null;
				{
					String query;
					{
						InputStreamReader is =  new InputStreamReader(t.getRequestBody(),"utf-8");
						BufferedReader br = new BufferedReader(is);
						StringBuilder buf = new StringBuilder(512);
						int b;
						while ((b = br.read()) != -1) {
							buf.append((char) b);
						}
						query = buf.toString();
					}
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
						if (sessionID != null) {
							Headers headers = t.getResponseHeaders();
							headers.set("Set-Cookie", String.format("%s=%s; path=/app", "sessionID", sessionID));
							headers.set("Location", "/app");

							t.sendResponseHeaders(303, -1);
						} else {
							HttpErrors.send500(t);
						}
					} else {
						HttpErrors.send401(t);
					}
				} catch (SQLException e) {
					System.out.println("Error: "+e.getMessage());
					e.printStackTrace();
					HttpErrors.send500(t);
				}
			}
		}
	}

	static class AppHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			// Get username and sessionID cookies
			String sessionID = null;
			{
				Headers header = t.getRequestHeaders();
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
					t.getResponseHeaders().set("Location", "/");
					t.sendResponseHeaders(303, -1);
					return;
				}
			}
			if (Login.validCookie(sessionID)) {
				sendHtmlFile(t, "/app/app.html");
			} else {
				t.getResponseHeaders().set("Location", "/");
				t.sendResponseHeaders(303, -1);
			}
		}
	}

	private static void sendHtmlFile(HttpExchange t, String path) {
		try {
			File root = new File(new File(".").getCanonicalPath() + "/src/web");
			File file = new File(root + path).getCanonicalFile();
			if (!file.exists()) throw new IOException("File '" + file + "' not found!");

			t.sendResponseHeaders(200, 0);
			OutputStream os = t.getResponseBody();
			FileInputStream fs = new FileInputStream(file);
			final byte[] buffer = new byte[0x10000];
			int count = 0;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer,0,count);
			}
			fs.close();
			os.close();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			HttpErrors.send500(t);
		}

	}
}