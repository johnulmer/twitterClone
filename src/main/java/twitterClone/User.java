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
	public static int GetUserByUserName(String userName) {
		int userID = -1;
		User u = new User();
		String sqlString = "SELECT UserID FROM Users WHERE UserName = '" + userName + "'";
		System.out.println(sqlString);
        try (Connection conn = u.connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
               while (rs.next()) {
            	   userID = rs.getInt("UserID");
               }
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		return userID;
	}
	// Read user from DB by userName
	public static int GetUserByHandle(String handle) {
		int userID = -1;
		User u = new User();
		String sqlString = "SELECT UserID FROM Users WHERE Handle = '" + handle + "'";
		System.out.println(sqlString);
        try (Connection conn = u.connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
               while (rs.next()) {
            	   userID = rs.getInt("UserID");
               }
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		return userID;
	}
	// Register new user if neither username nor handle are in use
	public static String Register(String userName, String password, String handle) {
		String returnString = "";
		if (User.GetUserByHandle(handle) != -1) {
			returnString = "Handle exists, you are a copycat.";
		} else if (User.GetUserByUserName(userName) != -1) {
			returnString = "Sandra Bullock opposes your identity theft - try a different User Name.";
		} else {
			ProcessRegistration(userName, password, handle);
			returnString = "SUCCESS";
		}
		return returnString;
	}
	
	public static void ProcessRegistration(String userName, String password, String handle) {
		User u = new User();
		String sqlString = "INSERT INTO Users (UserName, Password, Handle) VALUES " +
				"('" + userName + "',  '" + password + "', '" + handle + "')";
		System.out.println(sqlString);
        try (Connection conn = u.connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
        		//System.out.println("working");
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		//System.out.println("reading user");
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
	// Authenticate user based on userName and password
	public static int Authenticate(String userName, String password) {
		User u = new User();
		int userID = -1;
		String sqlString = "SELECT UserID FROM Users WHERE UserName = '" + userName + "' AND Password = '" + password + "'";
		System.out.println(sqlString);
        try (Connection conn = u.connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
               
               // loop through the result set
               while (rs.next()) {
            	   userID = rs.getInt("UserID");
               }
               System.out.println("found ID: " + userID);
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		System.out.println("reading user");
		return userID;
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
