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

@WebSocket
public class ServerSocketHandler {
	private final static HashMap<String, ServerSocketHandler> sockets = new HashMap<>();
	private Session session = null;
	private String userID = null;

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.println("Close: statusCode: " + statusCode + ", reason: " + reason);

		// remove connection
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
		try {
			session.getRemote().sendString("Hello Webbrowser");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@OnWebSocketMessage
	public void onMessage(String message) throws IOException {
		System.out.println("Message: " + message);
		JSONObject jsonMessage = new JSONObject(message);

		JSONObject response = new JSONObject();
		if (userID == null) {
			if (jsonMessage.get("command").equals("greetings")) {
				try {
					this.userID = DatabaseConnection.getUser(jsonMessage.get("sessionID").toString());

					// map this userID to this connection
					ServerSocketHandler.sockets.put(this.userID, this);

					response.put("status", "ok");
					response.put("command", "greetings");
				} catch (SQLException e) {
					response.put("status", "error");
					response.put("errorMessage", "Internal server error");
				} catch (Exception e) {
					response.put("status", "error");
					response.put("errorMessage", e.getMessage());
				}
			} else {
				response.put("status", "error");
				response.put("errorMessage", "Not logged in");
			}
		} else {
			// o usuário está logado, envia os dados que ele mandou ao Input
			response = Input.process(jsonMessage);
		}
		this.sendClient(response.toString());
	}

	private void sendClient(String message) throws IOException {
		this.session.getRemote().sendString(message);
	}

	static void send(String userID, String message) throws IOException {
		// is the destination client connected?
		if (ServerSocketHandler.sockets.containsKey(userID)) {
			// get clientSession object from socketsMap
			ServerSocketHandler clientSession = sockets.get(userID);
			clientSession.sendClient(message.toString());
		}
	}
}