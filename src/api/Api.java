package api;

import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import database.DatabaseConnection;
import database.MongoConnection;

class Api {
	static JSONObject process(JSONObject data) {
		System.out.println("Message: " + data.toString());
		JSONObject response = new JSONObject();

		Map<String, Runnable> commands = new HashMap<>();
		// Comandos reconhecidos (ok)
		commands.put("hello", () -> {
			response.put("status", "ok");
			response.put("info", "hi");
		});

		commands.put("send", () -> {
			String sender = data.getString("userID");
			String receiver = data.getString("receiver");
			String message =   data.getString("message");
			if (sender.equals(receiver)) { commands.get("badRequest").run(); return; }
			try {
				JSONObject mail = new JSONObject();
				mail.put("status", "ok");
				mail.put("command", "newMessage");
				mail.put("sender", sender);
				mail.put("message", message);
				ServerSocketHandler.send(receiver, mail.toString());

				Document messageDoc = new Document();
				messageDoc.put("sender", sender);
				messageDoc.put("message", message);
				messageDoc.put("timestamp", data.get("timestamp"));
				if(data.has("file")){
					byte[] file = Base64.getDecoder().decode(data.getString("file"));
					messageDoc.put("file", file);
					messageDoc.put("fileExtension", data.get("fileExtension"));
				}

				MongoConnection.addMessage(messageDoc, sender, receiver);
			} catch (IOException e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getUserList", () -> {
			try {
				response.put("status", "ok");
				response.put("command", "getUserList");
				response.put("userList", new JSONArray(DatabaseConnection.getUserList()));
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getMessages", () ->  {
			try {
				response.put("status", "ok");
				response.put("command", "getMessages");
				String sender = data.getString("userID");
				String receiver = data.getString("receiver");
				List<Document> messageBatch;
				if (data.has("lastID")) {
					String lastID = data.getString("lastID");
					messageBatch = MongoConnection.getMessageBatch(lastID, sender, receiver);
				} else {
					messageBatch = MongoConnection.getMessageBatch("0", sender, receiver);
				}
				JSONArray messageList = new JSONArray();
				messageBatch.forEach(message -> messageList.put(new JSONObject(message.toJson())));
				response.put("messageList", messageList);
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
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
