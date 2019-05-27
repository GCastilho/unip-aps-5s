package api;

import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import database.DatabaseConnection;
import database.MongoConnection;

class Api {
	static JSONObject process(JSONObject data) {
		JSONObject response = new JSONObject();

		Map<String, Runnable> commands = new HashMap<>();
		// Comandos reconhecidos (ok)
		commands.put("hello", () -> {
			response.put("status", "ok");
			response.put("info","hi");
		});

		commands.put("send", () -> {
			String sender = data.getString("sender");
			String receiver = data.getString("receiver");
			String message =   data.getString("message");
			try {
				JSONObject mail = new JSONObject();
				mail.put("status", "ok");
				mail.put("command", "newMessage");
				mail.put("message", message);
				ServerSocketHandler.send(receiver, mail.toString());

				Document messageDoc = new Document();
				messageDoc.put("sender", sender);
				messageDoc.put("receiver", receiver);
				messageDoc.put("message", message);
				messageDoc.put("timestamp", data.getString("timestamp"));

				MongoConnection.addMessage(messageDoc, sender, receiver);

				response.put("status", "ok");
				response.put("command", "response");
				response.put("sended", true);
			} catch (IOException e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getUserList", () -> {
			try {
				response.put("status", "ok");
				response.put("command", "response");
				response.put("userList", new JSONArray(DatabaseConnection.getUserList()));
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getMessages", () -> {
			response.put("status", "error");
			response.put("info", "command not implemented");
		});

		// Comandos de erro
		commands.put("commandNotFound", () -> {
			response.put("status", "error");
			response.put("info", "Command not found");
		});

		commands.put("badRequest", () -> {
			response.put("status", "error");
			response.put("info", "Bad request");
		});

		commands.put("internalServerError", () -> {
			response.put("status", "error");
			response.put("info", "Internal server error");
		});

		if (data.has("command")) {
			commands.getOrDefault(data.getString("command"), commands.get("commandNotFound")).run();
		} else {
			commands.get("badRequest").run();
		}

		return response;
	}
}
