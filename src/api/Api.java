package api;

import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				mail.put("timestamp", data.get("timestamp"));
				if (DatabaseConnection.isGroup(receiver)) {
					DatabaseConnection.getGroupUserList(receiver)
							.forEach(user -> {
								try{
									ServerSocketHandler.send(user, mail.toString());
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
				} else {
					ServerSocketHandler.send(receiver, mail.toString());
				}

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

		commands.put("getChatList", () -> {
			try {
				response.put("status", "ok");
				response.put("command", "getChatList");
				List<String> userList = DatabaseConnection.getUserList();
				List<String> groupsList = DatabaseConnection.getUserGroupList(data.getString("userID"));

				response.put("userList", new JSONArray(userList));
				response.put("groupsList", new JSONArray(groupsList));
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getChatUserList", () -> {
			try {
				String user = data.getString("userID");
				response.put("status", "ok");
				response.put("command", "getUserChatList");
				response.put("userList", new JSONArray(DatabaseConnection.getUserGroupList(user)));
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("newGroup", () -> {
			try {
				String group = data.getString("groupName");
				JSONArray arrJson = data.getJSONArray("users");
				String[] users = new String[arrJson.length()];
				for(int i = 0; i < arrJson.length(); i++) {
					users[i] = arrJson.getString(i);
				}

				response.put("status", "ok");
				response.put("command", "newGroup");
				response.put("group",group);
				response.put("users",users);

				DatabaseConnection.createGroup(group,users);
			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("addUserToGroup", () -> {
			try {
				String group = data.getString("groupName");
				String user = data.getString("user");
				response.put("status", "ok");
				response.put("command", "addUserToGroup");
				response.put("user",user);
				DatabaseConnection.addGroupUser(group,user);

			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("removeUserFromGroup", () -> {
			try {
				String group = data.getString("groupName");
				String user = data.getString("user");
				response.put("status", "ok");
				response.put("command", "addGroup");
				response.put("user",user);
				DatabaseConnection.removeGroupUser(group,user);

			} catch (Exception e) {
				e.printStackTrace();
				commands.get("internalServerError").run();
			}
		});

		commands.put("getGroupUsers", () -> {
			try {
				String group = data.getString("groupName");
				response.put("status", "ok");
				response.put("command", "addGroup");

				DatabaseConnection.getGroupUserList(group);
				response.put("user",DatabaseConnection.getGroupUserList(group));
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
