package twitterClone;

import java.util.ArrayList;

public class User {

	private String userName;
	private String password;
	private String handle;
	private int userID;
	
	// Constructors
	
	// this constructor is used for registration - no ID until user is stored in the DB
	public User(String userName, String password, String handle) {
		this.userName = userName;
		this.password = password;
		this.handle = handle;
	}
	
	// this constructor is used to build user when all values are known, such as when reading from the Users table
	public User(String userName, String password, String handle, int userid) {
		this.userName = userName;
		this.password = password;
		this.handle = handle;
		this.userID = userid;
	}
	
	// this constructor is used when only the userID is known which is used to relate users to other objects like tweets & replies
	public User(int userID) {
		this.userID = userID;
		User u = this.getUserByID(userID);
		this.userName = u.getUserName();
		this.handle = u.getHandle();
		this.password = u.getPassword();
	}

	// plain constructor to return a user object, no data known
	public User() {
	}
	
	// register new user, authenticate << either required for access to the rest of the site
	public void register() {
		this.userID = TwitterDB.processRegistration(this.getUserName(), this.getPassword(), this.getHandle());
	}
	public User authenticate() {
		return TwitterDB.authenticate(this);
	}
	
	// Mutator methods for Profile update page (can't update userName)
	public void changeHandle(String newHandle) {
		this.handle = newHandle;
	}
	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
	public ArrayList<User> getFollowers(int userID) {
		return TwitterDB.myFollowers(userID);
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

	public String updateUser(String newHandle, String newPassword) {
		String returnString = "";
		if (TwitterDB.getUserByHandle(newHandle) != -1) {
			returnString = "I'm sorry, that handle is already in use.";
		} else if (TwitterDB.getUserByUserName(this.userName) != -1) {
			TwitterDB.updateUser(this.userID, newPassword, newHandle);
			returnString = "SUCCESS";
		}
		return returnString;
	}

	// Register new user if neither username nor handle are in use
	public String validNewUser() {
		String returnString = "";
		if (TwitterDB.getUserByHandle(this.handle) != -1) {
			returnString = "Handle exists, you are a copycat.";
		} else if (TwitterDB.getUserByUserName(this.userName) != -1) {
			returnString = "Sandra Bullock opposes your identity theft - try a different User Name.";
		} else {
			this.userID = TwitterDB.processRegistration(this.userName, this.password, this.handle);
			this.followUser(this.userID);  // user follows userself for feeds to work
			returnString = "SUCCESS";
		}
		return returnString;
	}
	
	public void followUser(int userID) {
		TwitterDB.followAnotherUser(this.getUserID(), userID);
	}
	public void stopFollowingUser(int userID) {
		TwitterDB.stopFollowingUser(this.getUserID(), userID);
	}
	public void blockUser(int userIDToBlock) {
		TwitterDB.blockUser(this.getUserID(), userIDToBlock);
	}
	
	//  getters
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
