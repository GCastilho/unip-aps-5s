package api;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Input {
	public static JSONObject process(JSONObject data) {
		JSONObject response = new JSONObject();

		Map<String, Runnable> commands = new HashMap<>();
		// Comandos reconhecidos (ok)
		commands.put("hello", () -> {
			response.put("status", "ok");
			response.put("message", "hi");
		});

		commands.put("send", () -> {
			response.put("status", "ok");
			response.put("sended", true);
			//sender ser sessionid é temporário
			ServerSocketHandler.send(data.get("sessionID").toString(), data.get("message").toString());
		});

		// Comandos de erro
		commands.put("commandNotFound", () -> {
			response.put("status", "error");
			response.put("errorMessage", "Command not found");
		});

		commands.put("badRequest", () -> {
			response.put("status", "error");
			response.put("errorMessage", "Bad request");
		});

		if (data.has("command")) {
			commands.getOrDefault(data.get("command").toString(), commands.get("commandNotFound")).run();
		} else {
			commands.get("badRequest").run();
		}

		return response;
	}
}
