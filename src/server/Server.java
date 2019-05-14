import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new RootHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Server is up and running on port 8000");
	}

	static class RootHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			if (t.getRequestMethod().equals("GET")) {
				File root = new File(new File(".").getCanonicalPath() + "/src/server/web");
				File file = new File(root + "/index.html").getCanonicalFile();
				t.sendResponseHeaders(200, 0);
				OutputStream os = t.getResponseBody();
				FileInputStream fs = new FileInputStream(file);
				final byte[] buffer = new byte[0x10000];
				int count = 0;
				while ((count = fs.read(buffer)) >= 0) {
					os.write(buffer,0,count);
				}
				fs.close();
				os.close();
			} else if (t.getRequestMethod().equals("POST")) {
				String username = null;
				String password = null;
				{
					String query;
					{
						InputStreamReader is =  new InputStreamReader(t.getRequestBody(),"utf-8");
						BufferedReader br = new BufferedReader(is);
						StringBuilder buf = new StringBuilder(512);
						int b;
						while ((b = br.read()) != -1) {
							buf.append((char) b);
						}
						query = buf.toString();
					}
					String[] array = query.split("&");
					for (String str : array) {
						String[] pair = str.split("=");
						if (pair[0].equals("username")) {
							username = pair[1];
						} else if (pair[0].equals("password")) {
							password = pair[1];
						}
					}
				}
				//call validadepassword with username and password
				//call getUserCookie with username and password

				System.out.println(username);
				System.out.println(password);

				String response = "Hello POST";
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

}