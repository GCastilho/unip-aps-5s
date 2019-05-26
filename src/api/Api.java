package api;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
			try {
				ServerSocketHandler.send(data.getString("receiver"), data.getString("message"));
				response.put("status", "ok");
				response.put("sended", true);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("status", "error");
				response.put("info", "Internal server error");
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
