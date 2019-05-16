package login;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Date;

public class Login {
	public static boolean validCredentials(String username, String password){
		password = getSHA512(password);
		String query = "select id from credential where username = '"+username+"' and password_hash ='"+password+"'";
		Connection con = DatabaseConnection.getConnection();

		try {
			//uses the statatement object that returned from createStatement() to use the executequery() method
			//the resultset returned execute next() to verify if the first row of the select is empty
			return con.createStatement().executeQuery(query).next();

		}catch(SQLException e){
				e.printStackTrace();
				return false;
		}
	}

	public static boolean validCookie(String sessionId){
		String query = "select timestamp from cookie where sessionId ='"+sessionId+"'";
		Connection con = DatabaseConnection.getConnection();
		try {

			ResultSet rs = con.createStatement().executeQuery(query);
			if(rs.next()){
				//verify if cookie timestamp is bigger than 10 minutes(in miliseconds)
				return rs.getLong(1)> new Date().getTime()-600000;
			}

		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	public static String makeCookie(String user, String password){
		Connection con = DatabaseConnection.getConnection();
		long time = new Date().getTime();

		password =  getSHA512(password+user+time);

		String query = " insert into cookie (username,sessionID,timeStamp)"
		+ " values (?,?,?)";
		try {
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1,user );
			preparedStmt.setString (2,password);
			preparedStmt.setLong (3,time);

			preparedStmt.execute();

			System.out.println("COOKIE adicionada com sucesso ao banco de dados");
			return password;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getSHA512(String input){
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			return (String.format("%0128x", new BigInteger(1, digest.digest())));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
