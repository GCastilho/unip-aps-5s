import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import http.HttpRootHandler;
import http.HttpAppHandler;

public class Aps_5s {
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
		httpServer.createContext("/", new HttpRootHandler());
		httpServer.createContext("/app", new HttpAppHandler());
		httpServer.setExecutor(null); // creates a default executor
		httpServer.start();
		System.out.println("HTTP server is up and running on port 8000");

		Server apiServer = new Server(8080);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(api.ServerSocketHandler.class);
				factory.getPolicy().setIdleTimeout(36000000);   //10 Minutes
			}
		};
		apiServer.setHandler(wsHandler);
		apiServer.start();
		//api_server.join();
		System.out.println("API server is up and running on port 8080");
	}
}