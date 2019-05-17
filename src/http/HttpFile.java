package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpFile {
	private static void sendFile(HttpExchange httpExchange, File file) {
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
			HttpErrors.send404(httpExchange);
		}
	}

	public static void sendHtml(HttpExchange httpExchange, String path) {
		try {
			File root = new File(new File(".").getCanonicalPath() + "/src/web");
			File file = new File(root + path).getCanonicalFile();
			if (!file.exists()) throw new IOException("File '" + file + "' not found!");

			sendFile(httpExchange, file);
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			HttpErrors.send500(httpExchange);
		}
	}

	public static void sendRaw(HttpExchange httpExchange, String path) {
		try {
			File root = new File(new File(".").getCanonicalPath() + "/src/web");
			File file = new File(root + path).getCanonicalFile();

			sendFile(httpExchange, file);
		} catch (IOException e) {
			System.out.print("Error while trying to send file to user: ");
			System.out.println(e.getMessage());
		}
	}
}
