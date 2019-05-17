package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	public static String status = "not conected";

	public DatabaseConnection() {}

	public static java.sql.Connection getConnection() {

		Connection connection = null;

	try {
		Class.forName("com.mysql.cj.jdbc.Driver");

		String serverName = "127.0.0.1:3306";    //caminho do servidor do BD
		String mydatabase = "unip_aps_5s"; //nome do seu banco de dados
		String url = "jdbc:mysql://" + serverName + "/"+mydatabase+"?useTimezone=true&serverTimezone=UTC";
		String username = "root";        //nome de um usuário de seu BD
		String password = "root";      //sua senha de acesso
			
		connection = DriverManager.getConnection(url, username, password);
		if (connection != null) {

				status = ("STATUS--->conection sucessfull!");

			} else {

				status = ("STATUS--->conection failed");

			}
			return connection;

		} catch (ClassNotFoundException e) {  //Driver não encontrado

			System.out.println("O driver expecificado nao foi encontrado.");

			return null;
		} catch (SQLException e) {
			System.out.println("Nao foi possivel conectar ao Banco de Dados.");

			return null;
		}
	}

	public static boolean EndConection() {
		try {
			DatabaseConnection.getConnection().close();
			return true;

		} catch (SQLException e) {
			return false;
		}
	}

	public static java.sql.Connection RestartConnection() {
		EndConection();
		return DatabaseConnection.getConnection();
	}
}