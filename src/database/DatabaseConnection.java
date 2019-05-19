package database;

import java.sql.*;

public class DatabaseConnection {
	private static Connection getConnection() throws SQLException,ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");

		String serverName = "127.0.0.1:3306";		//caminho do servidor do BD
		String mydatabase = "unip_aps_5s";			//nome do seu banco de dados
		String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?useTimezone=true&serverTimezone=UTC";
		String username = "aps";					//nome de um usuário de seu BD
		String password = "";						//sua senha de acesso

		return DriverManager.getConnection(url, username, password);
	}

	static ResultSet executeQuery(String query) throws SQLException,ClassNotFoundException {
		// TODO: Fechar a conexão
		return getConnection().createStatement().executeQuery(query);
	}

	static PreparedStatement getPreparedStatment(String query) throws SQLException,ClassNotFoundException {
		Connection connection = getConnection();

		return connection.prepareStatement(query);
	}
}