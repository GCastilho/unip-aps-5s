package api;

import com.mongodb.*;
import database.MongoConnection;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Api {
	public static JSONObject process(JSONObject data) {
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
				ServerSocketHandler.send(receiver, message);

				Document messageDoc = new Document();
				messageDoc.put("sender", sender);
				messageDoc.put("receiver", receiver);
				messageDoc.put("message", message);
				messageDoc.put("time", data.getString("time"));

				MongoConnection.addMessage(messageDoc, sender, receiver);

				response.put("status", "ok");
				response.put("sended", true);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("status", "error");
				response.put("errorMessage", "Internal server error");
			}
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

		if (data.has("command")) {
			commands.getOrDefault(data.getString("command"), commands.get("commandNotFound")).run();
		} else {
			commands.get("badRequest").run();
		}

		return response;
	}
}
