package api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Input {
	public static void process(String query) {
		System.out.println("'"+query+"'");

		// TODO: SEMPRE retornar um JSON
		// TODO: processar um comando de exemplo 'hi' e retornar 'ok'; bad request se outro
		Map<String, Runnable> commands = new HashMap<>();
		commands.put("michael", () -> {
			System.out.println("Hello Michael");
		});

		commands.put("commandNotFound", () -> {
			System.out.println("Command not found!");
		});

		commands.put("badRequest", () -> {
			System.out.println("Bad request");
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
	}
}
