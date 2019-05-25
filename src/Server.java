
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import http.HttpAppHandler;
import http.HttpRootHandler;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);


		server.createContext("/", new HttpRootHandler());//host site files
		server.createContext("/app", new HttpAppHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Server is up and running on port 8000");
	}

}