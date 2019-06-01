package api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import database.DatabaseConnection;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import javax.xml.crypto.Data;

@WebSocket
public class ServerSocketHandler {
	private final static HashMap<String, ServerSocketHandler> sockets = new HashMap<>();
	private Session session = null;
	private String userID = null;
	private String sessionID = null;

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.println("Close: statusCode: " + statusCode + ", reason: " + reason);

		// Remove cookie from database
		try {
			DatabaseConnection.dropCookie(this.sessionID);
		} catch (Exception e) {
			System.out.println("Error deleting cookie from database");
			e.printStackTrace();
		}

		// Remove socket from map
		ServerSocketHandler.sockets.remove(this.userID);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		// save session so we can send
		this.session = session;

		System.out.println("Connect: " + session.getRemoteAddress().getAddress());

	}

	@OnWebSocketMessage
	public void onMessage(String message) throws Exception {
		JSONObject jsonMessage = new JSONObject(message);

		JSONObject response = new JSONObject();
		if (userID == null) {
			if (jsonMessage.get("command").equals("greetings")) {
				try {
					String sessionId = jsonMessage.getString("sessionID");
					sessionId = sessionId.substring(sessionId.length()-128);
					this.userID = DatabaseConnection.getUser(sessionId);
					this.sessionID = sessionId;

					// map this userID to this connection
					ServerSocketHandler.sockets.put(this.userID, this);

					response.put("status", "ok");
					response.put("info", "greetings");
				} catch (SQLException e) {
					response.put("status", "error");
					response.put("command", "response");
					response.put("info", "Internal server error");
				} catch (Exception e) {
					response.put("status", "error");
					response.put("command", "response");
					response.put("info", e.getMessage());
				}
			} else {
				response.put("status", "error");
				response.put("info", "Not logged in");
			}
		} else {
			if (DatabaseConnection.validCookie(this.sessionID)) {
				DatabaseConnection.updateCookie(this.sessionID);
				jsonMessage.put("userID", userID);
				response = Api.process(jsonMessage);
			} else {
				response.put("status", "error");
				response.put("info", "Not logged in");
			}
		}
		if (!response.isEmpty()) this.sendClient(response.toString());
	}

	private void sendClient(String message) throws IOException {
		this.session.getRemote().sendString(message);
	}

	static void send(String userID, String message) throws IOException {
		// is the destination client connected?
		if (ServerSocketHandler.sockets.containsKey(userID)) {
			// get clientSession object from socketsMap
			ServerSocketHandler clientSession = sockets.get(userID);
			clientSession.sendClient(message);
		}
	}

	public static void sendAllOnline(String newUser) throws IOException {
		JSONObject mail = new JSONObject();
		mail.put("status", "ok");
		mail.put("command", "newUser");
		mail.put("user", newUser);
		ServerSocketHandler.sockets.forEach((user, socket) -> {
			try {
				socket.sendClient(mail.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}