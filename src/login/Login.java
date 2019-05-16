package login;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Date;

public class Login {
	public static boolean validCredentials(String user, String password){
		password = getSHA512(password);
		String query = "select count(id) from authentication where username = '"+user+"' and password_hash ='"+password+"'";
		Connection con = DatabaseConnection.getConexaoMySQL();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			while (rs.next()) {
			count = rs.getInt("count(id)");
			}
			return count == 1;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static String makeCookie(String user, String password){
		Connection con = DatabaseConnection.getConexaoMySQL();

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		password =  getSHA512(password+user+ts);

		String query = " insert into cookie (username,sessionID,timestamp)"
		+ " values (?,?,?)";
		try {
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1,user );
			preparedStmt.setString (2,password);
			preparedStmt.setString (3,""+ts+"");

			preparedStmt.execute();

			System.out.println("sessao adicionada com sucesso ao banco de dados");
			return password;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static boolean validCookie(String user, String sessionId){
		String query = "select count(id) from cookie where username = '"+user+"' and sessionId ='"+sessionId+"'";
		Connection con = DatabaseConnection.getConexaoMySQL();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("count(id)");
			}
			return count == 1;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
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
