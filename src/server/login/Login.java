package login;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Date;

public class Login {
	public static boolean validCredentiais(String user, String password){
		password = getSHA512(password);
		String query = "select count(id) from login_data where name = '"+user+"' and password_enc ='"+password+"'";
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

		String query = " insert into cookie (user_name,sessionID,timeStamp)"
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
		String query = "select count(id) from cookie where user_name = '"+user+"' and sessionId ='"+sessionId+"'";
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
