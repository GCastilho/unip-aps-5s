package database;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class DatabaseConnection {
	private static Connection conn;

	private static Connection getConnection() throws SQLException,ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");

		String serverName = "127.0.0.1:3306";        //caminho do servidor do BD
		String mydatabase = "unip_aps_5s";            //nome do seu banco de dados
		String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?useTimezone=true&serverTimezone=UTC";
		String username = "aps";					//nome de um usuário de seu BD
		String password = "";						//sua senha de acesso
		return conn = DriverManager.getConnection(url, username, password);
	}


	public static boolean validCredentials(String username, String password) throws Exception {
		password = getSHA512(password);
		try {
			PreparedStatement stmt = getConnection().prepareStatement(
					"select id from credential where username = ? and password_hash = ?");
			stmt.setString(1, username);
			stmt.setString(2, password);

			//the prepared statement returns a result set
			//the Result Set of the return executes next() to verify if the first row of the select is empty
			return stmt.executeQuery().next();
		} finally {
			conn.close();
		}
	}

	public static String makeCookie(String user, String password) throws Exception {
		long time = new Date().getTime();
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"insert into cookie (username,sessionID,timestamp) values (?,?,?)"
			);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, password = getSHA512(password + user + time));
			preparedStatement.setLong(3, time);

			preparedStatement.execute();

			return password;
		} finally {
			conn.close();
		}
	}

	public static void updateCookie(String sessionId) throws Exception {
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"update cookie set timestamp = ? where sessionID = ?"
			);
			preparedStatement.setLong(1, new Date().getTime());
			preparedStatement.setString(2, sessionId);

			preparedStatement.execute();
		} finally {
			conn.close();
		}
	}

	public static boolean signUp(String user, String password) throws Exception {
		try {
			//verifica se o usuario ja esta cadastrado
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"select * from credential where username = ?"
			);
			preparedStatement.setString(1, user);

			if(preparedStatement.executeQuery().next()){
				//retorna false se existe um usuario com esse username
				return false;
			}
			//adiciona o usuario no banco de dados
			//as linhas comentadas se referem a uma situacao em que salt ja esta implementado
			//salt no database é um varchar(30),nao sei se isso é pequeno ou grande
			preparedStatement = conn.prepareStatement(
					//"insert into credential (username,password_hash,salt) values (?,?,?)"
					"insert into credential (username,password_hash) values (?,?)"
			);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, getSHA512(password));
			//preparedStatement.setString(3, salt);

			preparedStatement.execute();

			return true;
		} finally {
			conn.close();
		}
	}


	public static boolean validCookie(String sessionId) throws Exception {
		try {
			ResultSet resultSet = getConnection().createStatement().executeQuery(
					"select timestamp from cookie where sessionId = '"+sessionId+"'"
			);

			//verify if cookie timestamp is bigger than 10 minutes (in milliseconds)
			return resultSet.next() && resultSet.getLong(1) > new Date().getTime()-600000;
		} finally {
			conn.close();
		}
	}
	public static String getUser(String sessionId) throws Exception {
		try {
			ResultSet resultSet = getConnection().createStatement().executeQuery(
					"select username from cookie where sessionId = '" + sessionId + "'"
			);
			if (resultSet.next()) {
				return resultSet.getString(1);
			} else {
				throw new Exception("Username not found for given sessionID");
			}
		} finally {
			conn.close ();
		}
	}
	public static List<String> getUserList() throws Exception {
		try {
			List<String> list = new ArrayList<>();
			ResultSet rs = getConnection().createStatement().executeQuery(
					"select username from credential"
			);
			while (rs.next()) list.add(rs.getString(1));

			//the List can later be cast into other List
			return list;
		} finally {
			conn.close();
		}
	}
	private static String getSHA512(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(input.getBytes(StandardCharsets.UTF_8));

		return (String.format("%0128x", new BigInteger(1, digest.digest())));
	}

}
