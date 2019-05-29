package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.json.JsonParseException;
import org.json.JSONObject;

import java.io.IOException;

public class HttpRegisterHandler implements HttpHandler {
	public void handle(HttpExchange httpExchange) throws IOException {
		if (httpExchange.getRequestMethod().equals("GET")) {
			if (httpExchange.getRequestURI().getPath().equals("/register")) {//
				Http.sendHtml(httpExchange, "/register/register.html");
			} else {
				Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
			}
		} else if (httpExchange.getRequestMethod().equals("POST")) {
			String username = null;
			String password = null;
			{
				String query = Http.getPOST(httpExchange.getRequestBody());
				try {
					JSONObject userdata = new JSONObject(query);
					System.out.println("new User: <" + userdata.get("user") +">" );
					System.out.println("Password: <" + userdata.get("password") +">" );
					System.out.println("Implementar criação de usuario no servidor");

				}catch ( JsonParseException e){
					System.out.println("Erro na conversao dos dados do novo usuario:\n\t" + query + "\n" +e.getMessage());
				}
			}

		}
	}
}

