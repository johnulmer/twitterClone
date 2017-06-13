package twitterClone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

	private String userName;
	private String password;
	private String handle;
	private int userID;
	
	// Constructor for registration page
	public User(String userName, String password, String handle) {
		super();
		this.userName = userName;
		this.password = password;
		this.handle = handle;
	}
	
	// no arg Constructor for loading User from DB
	public User() {

	}
	
	// Mutator methods for Profile update page (can't update userName)
	public void ChangeHandle(String newHandle) {
		this.handle = newHandle;
	}
	public void ChangePassword(String newPassword) {
		this.password = newPassword;
	}
	
	// Write user to DB
	public void SaveUser() {
		// db logic here
		String sqlString = "INSERT INTO Users (UserName, Password, Handle) VALUES (" 
				+ this.userName + ", " + this.password + "," + this.handle + ")";
		System.out.println(sqlString);
	}
	
	// Read user from DB by userName
	public static User GetUserByUserName(String userName) {
		User u = new User(userName, "test password", "test handle");
		u.userID = 0; // will read from DB, hard-coded for now
		System.out.println("reading user");
		return u;
	}
	// Read user from DB by userID
	public static User GetUserByUserID(int userID) {
		
		//User staticU = new User("test username", "test password", "test handle");
		User u = new User();
		String sqlString = "SELECT UserID, UserName, Password, Handle FROM Users WHERE UserID = " + userID;
        try (Connection conn = u.connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
               
               // loop through the result set
               while (rs.next()) {
                   System.out.println(rs.getInt("UserID") +  "\t" + 
                                      rs.getString("UserName") + "\t" +
                                      rs.getString("Handle"));
           			u = new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Handle"));
               }
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		System.out.println("reading user");
		return u;
	}
	
    private Connection connect() {
        // SQLite connection string
        String url = TwitterDB.DBURL;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	// generic getters if needed
	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getHandle() {
		return handle;
	}
}
