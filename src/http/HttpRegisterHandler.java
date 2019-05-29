package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import org.bson.json.JsonParseException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class HttpRegisterHandler implements HttpHandler {
	public void handle(HttpExchange httpExchange) throws IOException {
		if (httpExchange.getRequestMethod().equals("GET")) {
			if (httpExchange.getRequestURI().getPath().equals("/register")) {//
				Http.sendHtml(httpExchange, "/register/register.html");
			} else {
				Http.sendRaw(httpExchange, httpExchange.getRequestURI().getPath());
			}
		} else if (httpExchange.getRequestMethod().equals("POST")) {

			String query = Http.getPOST(httpExchange.getRequestBody());
			try {
				JSONObject userdata = new JSONObject(query);

				//envia os dados do usuario para cadastrar
				JSONObject dsend = new JSONObject();
				if(!DatabaseConnection.signUp(userdata.getString("user"), userdata.getString("password"))){
					//se usuario existir
					dsend.put("Status","Error");
					dsend.put("message","Usuario ja existe");
				} else {
					//se tiver sucesso ao cadastrar
					dsend.put("Status","ok");
					dsend.put("message","Usuario Cadastrado");
				}

				SendData(JSONObject.valueToString(dsend),httpExchange);
				httpExchange.close();

			} catch (JsonParseException e) {
				System.out.println("Erro na conversao dos dados do novo usuario:\n\t" + query + "\n" + e.getMessage());
			} catch (java.lang.Exception e) {
				System.out.println("Erro no cadastro de usuario:\n\tdata: " + query + "\n" + e.getMessage());
			}
		}
	}

	//função de envio de dados do post
	private void SendData(String data, HttpExchange httpExchange){
		try {
			httpExchange.getResponseHeaders().set("Content-Type", "text/plain");
			httpExchange.sendResponseHeaders(200, data.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(data.getBytes());
			os.close();
		}catch (IOException e){
			System.out.println("Erro no envio de dados ao cliente:\n\tdata: " + data.toString() + "\n" + e.getMessage());
		}
	}
}

