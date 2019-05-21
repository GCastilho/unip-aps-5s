package database;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;

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
			//the prepared statement returns a result set(
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
					"insert into cookie (username,sessionID,timestamp) values (?,?,?)");
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, password = getSHA512(password + user + time));
			preparedStatement.setLong(3, time);

			preparedStatement.execute();

			return password;
		} finally {
			conn.close();
		}
	}

	public static String updateCookie(String username, String sessionId) throws Exception {
		try {
			getConnection().createStatement().executeQuery(
					"delete from cookie where sessionId ='" + sessionId + "'");

		} finally {
			conn.close();
		}
		return makeCookie(username, sessionId);
	}


	public static boolean validCookie(String sessionId) throws Exception {
		try{
			ResultSet resultSet = getConnection().createStatement().executeQuery(
					"select timestamp from cookie where sessionId = '"+sessionId+"'");
			//verify if cookie timestamp is bigger than 10 minutes (in miliseconds)
			return resultSet.next() && resultSet.getLong(1) > new Date().getTime()-600000;
		}finally{
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
