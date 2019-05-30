package api;

import org.bson.Document;
import org.bson.types.Binary;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
			String sender = data.getString("userID");
			String receiver = data.getString("receiver");
			String message =   data.getString("message");
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
					//esse é um exemplo de como inserir um byteArray no database, eu nao sei como serao pegos
					messageDoc.put("file", new byte[]{1,2,3,4,5,6,7,8,9,5});
					//a linha abaixo é para ser usada caso o tipo de arquivo que sera recebido atravez de data for um byte[] ou algo do tipo
					//messageDoc.put("file", data.get("file"));
					//caso nao for reconhecido no mongoDatabase como binary, deve ser convertido para um byteArray ou similar
					messageDoc.put("fileExtension",data.get("fileExtension"));
				}

				MongoConnection.addMessage(messageDoc, sender, receiver);

				response.put("status", "ok");
				response.put("command", "response");
				response.put("response", "send");
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
				response.put("response", "getUserList");
				response.put("userList", new JSONArray(DatabaseConnection.getUserList()));
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getMessages", () -> {
			response.put("status", "ok");
			response.put("command", "response");
			response.put("response", "getMessages");
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
