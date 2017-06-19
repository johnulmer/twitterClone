package twitterClone;

import java.util.ArrayList;

public class User {

	private String userName;
	private String password;
	private String handle;
	private int userID;
	
	// Constructors
	public User(String userName, String password, String handle) {
		this.userName = userName;
		this.password = password;
		this.handle = handle;
	}
	public User(String userName, String password, String handle, int userid) {
		this.userName = userName;
		this.password = password;
		this.handle = handle;
		this.userID = userid;
	}
	public User(int userid) {
		this.GetUserByID(userid);
	}
	public User() {
	}
	
	//
	public void Register() {
		this.userID = TwitterDB.ProcessRegistration(this.getUserName(), this.getPassword(), this.getHandle());
	}
	public void Authenticate() {
		this.userID = TwitterDB.Authenticate(this.userName, this.password);
		this.handle = TwitterDB.GetUserHandle(this);
	}
	
	// Mutator methods for Profile update page (can't update userName)
	public void ChangeHandle(String newHandle) {
		this.handle = newHandle;
	}
	public void ChangePassword(String newPassword) {
		this.password = newPassword;
	}
	
	public ArrayList<User> GetFollowedUsers() {
		return TwitterDB.FollowedUsers(this.userID);
	}
	
	public ArrayList<User> GetUnfollowedUsers() {
		return TwitterDB.UnfollowedUsers(this.userID);
	}
	
	private User GetUserByID(int userid) {
		return TwitterDB.GetUserByUserID(userid);
	}
	
	// Write user to DB
	public void SaveUser() {
		// db logic here
		String sqlString = "INSERT INTO Users (UserName, Password, Handle) VALUES (" 
				+ this.userName + ", " + this.password + "," + this.handle + ")";
		System.out.println(sqlString);
	}

	// Register new user if neither username nor handle are in use
	public String ValidNewUser() {
		String returnString = "";
		if (TwitterDB.GetUserByHandle(this.handle) != -1) {
			returnString = "Handle exists, you are a copycat.";
		} else if (TwitterDB.GetUserByUserName(this.userName) != -1) {
			returnString = "Sandra Bullock opposes your identity theft - try a different User Name.";
		} else {
			TwitterDB.ProcessRegistration(this.userName, this.password, this.handle);
			returnString = "SUCCESS";
		}
		return returnString;
	}
	
	// generic getters
	public String getUserName() {
		return this.userName;
	}
	public String getPassword() {
		return this.password;
	}
	public String getHandle() {
		return this.handle;
	}
	public int GetUserID() {
		return this.userID;
	}
}
