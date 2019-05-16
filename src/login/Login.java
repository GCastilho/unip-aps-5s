package login;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Date;

public class Login {
	public static boolean validCredentials(String username, String password) throws SQLException {
		password = getSHA512(password);
		String query = "select id from credential where username = '"+username+"' and password_hash ='"+password+"'";
		Connection con = DatabaseConnection.getConnection();

		//uses the statement object that returned from createStatement() to use the executequery() method
		//the resultset returned execute next() to verify if the first row of the select is empty
		return con.createStatement().executeQuery(query).next();
	}

	public static boolean validCookie(String sessionId) throws SQLException {
		String query = "select timestamp from cookie where sessionId ='"+sessionId+"'";
		Connection con = DatabaseConnection.getConnection();

		ResultSet rs = con.createStatement().executeQuery(query);

		//verify if cookie timestamp is bigger than 10 minutes(in miliseconds)
		return rs.next() ? rs.getLong(1) > new Date().getTime()-600000 : false;
	}

	public static String updateCookie(String username ,String sessionId) throws SQLException {
		String query = "delete from cookie where sessionId ='"+sessionId+"'";
		DatabaseConnection.getConnection().createStatement().executeQuery(query);

		return makeCookie(username, sessionId);
	}

	public static String makeCookie(String user, String password) throws SQLException {
		Connection con = DatabaseConnection.getConnection();
		long time = new Date().getTime();

		password =  getSHA512(password+user+time);

		String query = "insert into cookie (username,sessionID,timeStamp)"
		+ " values (?,?,?)";

		PreparedStatement preparedStmt = con.prepareStatement(query);
		preparedStmt.setString(1,user );
		preparedStmt.setString(2,password);
		preparedStmt.setLong(3,time);

		preparedStmt.execute();

		return password;
	}

	private static String getSHA512(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes(StandardCharsets.UTF_8));
			return (String.format("%0128x", new BigInteger(1, digest.digest())));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
