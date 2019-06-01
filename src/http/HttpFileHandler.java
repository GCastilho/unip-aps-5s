package http;

import java.util.Base64;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import database.DatabaseConnection;
import database.MongoConnection;

public class HttpFileHandler implements HttpHandler {
	public void handle(HttpExchange httpExchange) {
		try {
			String sessionID = Http.getCookie("sessionID", httpExchange
					.getRequestHeaders().get("Cookie"));
			if (DatabaseConnection.validCookie(sessionID)) {
				if (httpExchange.getRequestMethod().equals("GET")) {
					String userID = DatabaseConnection.getUser(sessionID);
					String receiver = httpExchange.getRequestURI().getQuery().split("&")[1];
					String fileID = httpExchange.getRequestURI().getQuery().split("&")[0];
					byte[] file = Base64.getDecoder().decode(
							MongoConnection.getFile(fileID, userID, receiver).getString("file")
					);

					if (file.length > 0) {
						httpExchange.sendResponseHeaders(200, file.length);
						OutputStream os = httpExchange.getResponseBody();
						os.write(file);
						os.close();
					} else {
						Http.send404(httpExchange);
					}
				}
			} else {
				Http.send401(httpExchange);
			}
		} catch (Exception e) {
			Http.send500(httpExchange);
		}
	}
}
