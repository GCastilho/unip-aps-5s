package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Http {
	private static void send(HttpExchange httpExchange, File file) {
		try {
			if (!file.exists()) throw new IOException("File '" + file + "' not found!");

			httpExchange.sendResponseHeaders(200, file.length());
			OutputStream os = httpExchange.getResponseBody();
			FileInputStream fs = new FileInputStream(file);
			final byte[] buffer = new byte[0x10000];
			int count;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer,0,count);
			}
			fs.close();
			os.close();
		} catch (IOException e) {
			send404(httpExchange);
		}
	}

	private static void send(HttpExchange httpExchange, String response, int statusCode) {
		try {
			httpExchange.sendResponseHeaders(statusCode, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getCause());
			e.printStackTrace();
		}
	}

	public static void send401(HttpExchange httpExchange) {
		String response = "401 Unauthorized";
		send(httpExchange, response, 401);
	}

	public static void send404(HttpExchange httpExchange) {
		String response = "404 Not Found";
		send(httpExchange, response, 404);
	}

	public static void send500(HttpExchange httpExchange) {
		String response = "500 Internal Server Error";
		send(httpExchange, response, 500);
	}

	public static void sendHtml(HttpExchange httpExchange, String path) {
		try {
			File root = new File(new File(".").getCanonicalPath() + "/src/web");
			File file = new File(root + path).getCanonicalFile();
			if (!file.exists()) throw new IOException("File '" + file + "' not found!");

			send(httpExchange, file);
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			send500(httpExchange);
		}
	}

	public static void sendRaw(HttpExchange httpExchange, String path) {
		try {
			File root = new File(new File(".").getCanonicalPath() + "/src/web");
			File file = new File(root + path).getCanonicalFile();

			send(httpExchange, file);
		} catch (IOException e) {
			System.out.print("Error while trying to send file to user: ");
			System.out.println(e.getMessage());
		}
	}

	public static String getPOST(InputStream httpBody) throws IOException {
		InputStreamReader is =  new InputStreamReader(httpBody, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(is);
		StringBuilder buf = new StringBuilder(512);
		int b;
		while ((b = br.read()) != -1) {
			buf.append((char) b);
		}
		return buf.toString();
	}
}
