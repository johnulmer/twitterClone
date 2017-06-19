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
		this.getUserByID(userid);
	}
	public User() {
	}
	
	//
	public void register() {
		this.userID = TwitterDB.processRegistration(this.getUserName(), this.getPassword(), this.getHandle());
	}
	public void authenticate() {
		this.userID = TwitterDB.authenticate(this.userName, this.password);
		this.handle = TwitterDB.getUserHandle(this);
	}
	
	// Mutator methods for Profile update page (can't update userName)
	public void changeHandle(String newHandle) {
		this.handle = newHandle;
	}
	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
	
	public ArrayList<User> getFollowedUsers() {
		return TwitterDB.followedUsers(this.userID);
	}
	
	public ArrayList<User> getUnfollowedUsers() {
		return TwitterDB.unfollowedUsers(this.userID);
	}
	
	private User getUserByID(int userid) {
		return TwitterDB.getUserByUserID(userid);
	}
	
	// Write user to DB
	public void saveUser() {
		// db logic here
		String sqlString = "INSERT INTO Users (UserName, Password, Handle) VALUES (" 
				+ this.userName + ", " + this.password + "," + this.handle + ")";
		System.out.println(sqlString);
	}

	// Register new user if neither username nor handle are in use
	public String validNewUser() {
		String returnString = "";
		if (TwitterDB.getUserByHandle(this.handle) != -1) {
			returnString = "Handle exists, you are a copycat.";
		} else if (TwitterDB.getUserByUserName(this.userName) != -1) {
			returnString = "Sandra Bullock opposes your identity theft - try a different User Name.";
		} else {
			TwitterDB.processRegistration(this.userName, this.password, this.handle);
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
	public int getUserID() {
		return this.userID;
	}
}
