package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	public static String status = "not conected";

	public DatabaseConnection() {}

	public static java.sql.Connection getConexaoMySQL() {

		Connection connection = null;//atributo do tipo Connection

	try {
// Carregando o JDBC Driver padrão

			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
// Configurando a nossa conexão com um banco de dados//

			String serverName = "127.0.0.1:3306";    //caminho do servidor do BD
			String mydatabase = "login"; //nome do seu banco de dados
			String url = "jdbc:mysql://" + serverName + "/"+mydatabase+"?useTimezone=true&serverTimezone=UTC";
			String username = "root";        //nome de um usuário de seu BD
			String password = "258o258O";      //sua senha de acesso
			
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
			//Não conseguindo se conectar ao banco
			System.out.println("Nao foi possivel conectar ao Banco de Dados.");
			return null;
		}
	}
    //Método que retorna o status da sua conexão//

	public static String statusConection() {

		return status;

    }
    //Método que fecha sua conexão//

	public static boolean FecharConexao() {

		try {

			DatabaseConnection.getConexaoMySQL().close();

			return true;

		} catch (SQLException e) {

			return false;
		}
	}
	//Método que reinicia sua conexão//
	public static java.sql.Connection ReiniciarConexao() {

		FecharConexao();

		return DatabaseConnection.getConexaoMySQL();
	}
}