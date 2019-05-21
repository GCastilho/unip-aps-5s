package api;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Input {
	public static String process(String query) {
		JSONObject response = new JSONObject();

		// Comandos reconhecidos (ok)
		Map<String, Runnable> commands = new HashMap<>();
		commands.put("hello", () -> {
			response.put("status", "ok");
			response.put("message", "hi");
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

		commands.put("notLoggedIn", () -> {
			response.put("status", "error");
			response.put("errorMessage", "Not logged in");
		});

		Map<String, String> map = new HashMap<>();
		Arrays.asList(query.split("&")).forEach(component ->
			map.put(component.split("=")[0], component.split("=")[1])
		);
		if (map.containsKey("command")) {
			commands.getOrDefault(map.get("command"), commands.get("commandNotFound")).run();
		} else {
			commands.get("badRequest").run();
		}


		for(Map.Entry<String, String> entry: map.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		return response.toString();
	}
}
