package twitterClone;

import static spark.Spark.*;
import java.util.ArrayList;


import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import spark.*;
//import com.google.gson.Gson;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;


public class TwitterRouter {
	
	private static void setSession(Request request, User u) {
		request.session().attribute("user", u);
	}
	
	// check session for every page in site except for log in page
	private static void checkSession(Request request) {
		User u = (User) request.session().attribute("user");
		if (u != null) {
		System.out.println("Page: " + request.url() + "     User: " + u.getUserName() + "   ID: " + u.getUserID());
		}
		// TODO change from void to boolean, check session(user.userID), return false if not greater than -1
		// return (u.getUserID() >= 0);
	}
	
	// present log in page with include used for heading
	private static String logIn(Request request, Response response) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
        JtwigModel model = JtwigModel.newModel();
        return template.render(model);
	}
	
	// present info about project, with include used for heading & menu
	private static String about(Request request, Response response) {
		checkSession(request);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/about.jtwig");
        JtwigModel model = JtwigModel.newModel();
        return template.render(model);
	}
	
	// check username and password to ensure valid user
	private static String authenticate(Request request, Response response) {
        User u = new User(request.queryParams("username"), 
        		request.queryParams("password"), 
        		"",
        		-1);
    	u = u.authenticate();
    	if (u.getUserID() > -1) {
    		setSession(request, u);
    		return "SUCCESS";
    	} else {
    		return "Unable to authenticate - please try again or register if you are a new user.";
    	}
	}
	// attempt to process registration for new site user or return message re: username or handle already in use
	private static String register(Request request, Response response) {
        User u = new User(request.queryParams("username"), 
        		request.queryParams("password"), 
        		request.queryParams("handle"));
        String registration = u.validNewUser();
        if (registration.equals("SUCCESS")) {
        	u.register();
        	setSession(request, u);
        }
    	return registration;
	}
	//  retrieve a User from session, display user update page
	private static String updateUser(Request request, Response response) {
		checkSession(request);
    	User u = (User) (request.session().attribute("user"));
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userUpdate.jtwig");
        JtwigModel model = JtwigModel.newModel().with("user", u);
        return template.render(model);
	}
	// retrieve a User from session & new password and handle from request, process if valid
	private static String updateHandle(Request request, Response response) {
    	String returnString = "";
		User u = (User) (request.session().attribute("user"));
    	returnString = u.updateHandle(request.queryParams("handle"));
    	if (returnString.equals("SUCCESS")) {
    		setSession(request, u);
    		return returnString;
    	} else {
    		return returnString;
    	}
	}
	// retrieve a User from session & new password and handle from request, process if valid
	private static String updatePassword(Request request, Response response) {
    	String returnString = "";
		User u = (User) (request.session().attribute("user"));
    	returnString = u.updatePassword(request.queryParams("password"));
    	if (returnString.equals("SUCCESS")) {
    		setSession(request, u);
    		return returnString;
    	} else {
    		return returnString;
    	}
	}
    //TO FOLLOW:
    // present page shell for users who are NOT followed
	//return userFollow(request, response);

    // return the specific list of users NOT followed by the logged in User
	// showUnFollowedUsers(request, response);

    // handle request to follow a specific user
    // followThisUser(request, response);
	
	// retrieve a User from session, show all users NOT followed by that User
	private static String userFollow(Request request, Response response) {
		checkSession(request);
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userFollow.jtwig");
	    JtwigModel model = JtwigModel.newModel();
	    return template.render(model);
	}
	// retrieve a User from session, show other users not followed by that user
	private static String showUnFollowedUsers(Request request, Response response) {
    	User u = (User) (request.session().attribute("user"));
		ArrayList<User> unfollowedUsers = u.getUnfollowedUsers();
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/UsersIDoNotFollow.jtwig");
	    JtwigModel model = JtwigModel.newModel().with("userlist", unfollowedUsers);
	    return template.render(model);
	}
	// retrieve a User from session & userID from request, follow that User
	private static String followThisUser(Request request, Response response) {
		int userToFollow = Integer.parseInt(request.queryParams("userid"));
		User u = (User) (request.session().attribute("user"));
		u.followUser(userToFollow);
		return "";
	}
	
    /* TO STOP FOLLOWING:
    // present page of users currently followed
    // userStopFollowing(request, response);

    // return the specific list of users who ARE followed by the logged in User
    // showFollowedUsers(request, response);

    // handle request to STOP following a specific user
    // stopFollowingThisUser(request, response); */

	// retrieve a User from session, show all users who ARE followed by that User
	private static String userStopFollowing(Request request, Response response) {
		checkSession(request);
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userStopFollow.jtwig");
        JtwigModel model = JtwigModel.newModel();
	    return template.render(model);
	}	
	
	// retrieve a User from session, show other users who ARE followed by that user
	private static String showFollowedUsers(Request request, Response response) {
    	User u = (User) (request.session().attribute("user"));
    	ArrayList<User> followedUsers = u.getFollowedUsers();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/UsersIFollow.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        return template.render(model);
	}
	
	// retrieve a User from session & userID from request, STOP following that User
	private static String stopFollowingThisUser(Request request, Response response) {
		int userToStopFollowing = Integer.parseInt(request.queryParams("userid"));
		User u = (User) (request.session().attribute("user"));
		u.stopFollowingUser(userToStopFollowing);
		return "";
	}
	
    //TO BLOCK:
    // show users currently following
    // showFollowers(request, response);
	
    // handle request to block a specific user
	// blockFollower(request, response);
    
	// retrieve a User from the session & show the users following that user
	private static String showFollowers(Request request, Response response) {
    	User u = (User) (request.session().attribute("user"));
    	ArrayList<User> followedUsers = u.getFollowers(u.getUserID());
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/UsersFollowingMe.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        //System.out.println(template.render(model));
        return template.render(model);
	}
	
	//  retrieve a User from session, display user update page
	private static String showTimelines(Request request, Response response) {
		checkSession(request);
    	User u = (User) (request.session().attribute("user"));
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/tweetsForUser.jtwig");
        JtwigModel model = JtwigModel.newModel().with("user", u);
        return template.render(model);
	}
	
	// retrieve a User from the session & show the users following that user
	private static String showFollowersSelect(Request request, Response response) {
    	User u = (User) (request.session().attribute("user"));
    	ArrayList<User> followedUsers = u.getFollowedUsers();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/selectUsersFollowingMe.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        //System.out.println(template.render(model));
        return template.render(model);
	}
	// retrieve a User from the session & show the users following that user
	private static String showNOTFollowersSelect(Request request, Response response) {
    	User u = (User) (request.session().attribute("user"));
    	ArrayList<User> followedUsers = u.getUnfollowedUsers();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/selectUsersNOTFollowingMe.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        //System.out.println(template.render(model));
        return template.render(model);
	}
	
	// retrieve a User from session & userID from request, block that User
	private static String blockFollower(Request request, Response response) {
		int userToBlock = Integer.parseInt(request.queryParams("userIDtoBlock"));
    	String returnString = "following userID but not committing: " + userToBlock;
		User u = (User) (request.session().attribute("user"));
		u.blockUser(userToBlock);
		return returnString;
	}

	//RAVI CODE STARTS

		// USED
		private static String tweetReply(Request request, Response response) {
			checkSession(request);
			int tweetID = Integer.parseInt(request.queryParams("tweetID"));
			int userID = Integer.parseInt(request.queryParams("userID"));
			System.out.println("tweetID "+ tweetID);
			//userID = 2; // RAVI, this needs to be removed

			String tweetReply = request.queryParams("tweetReply");
			System.out.println("tweetReply "+ tweetReply);
			try {
				Tweet insertReply = new Tweet();
				insertReply.connect();
				insertReply.insertReply(tweetID, userID, tweetReply);
				return "success";
			} catch (NumberFormatException ex) {
				System.out.println("bad input");
			}
			return "failure";
		};

//RAVI CHANGES START
		// USED
		private static String getTweet(Request request, Response response, String reqType) {
	    	User u = (User) (request.session().attribute("user"));
			Tweet getTweet = new Tweet();
			getTweet.connect();
			ArrayList<Tweet> tweetList;
			if (reqType.equals("main")) {
//				RAVI USERID RELATED CHANGE HERE
				System.out.println("called from timeline page - main" + u.getUserID());
				tweetList = getTweet.get(u.getUserID(), "main");
			} else {
				System.out.println("called from timeline page - own / otherwise" + u.getUserID());
				tweetList = getTweet.get(u.getUserID(), "own");
			}
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/enterTweet.jtwig");
			JtwigModel model = JtwigModel.newModel().with("tweets", tweetList);
			return template.render(model);
		};
//RAVI CHANGES END
		
		// USED
		private static String getTweets(Request request, Response response) {
//			ravi changes start here
//			int tweetID = Integer.parseInt(request.queryParams("tweetID"));
	    	User u = (User) (request.session().attribute("user"));
			String reqType =request.queryParams("reqType");
			System.out.println("reqType gettweets value " + reqType);
			Tweet getTweet = new Tweet();
			getTweet.connect();
			ArrayList<Tweet> tweetList;
//			if (reqType=="own") {
			if (reqType.equals("own")) {
//				ArrayList<Tweet> tweetList = getTweet.get(1, "own");
				tweetList = getTweet.get(u.getUserID(), "own");
			} else {
			    tweetList = getTweet.get(u.getUserID(), "main"); // returns
																	// tweetList
			}
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/tweets.jtwig");
			JtwigModel model = JtwigModel.newModel().with("tweets", tweetList);
			return template.render(model);
		};

		private static String getUserTimeline(Request request, Response response) {
			int userID =Integer.parseInt(request.queryParams("userID"));
			Tweet getTweet = new Tweet();
			getTweet.connect();
			ArrayList<Tweet> tweetList;
			tweetList = getTweet.get(userID, "own");
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/tweets.jtwig");
			JtwigModel model = JtwigModel.newModel().with("tweets", tweetList);
			return template.render(model);
		};
//		ravi changes end here
	
		// USED
		private static String insertTweet(spark.Request request, spark.Response response) {
//ravi changes start
			String tweetMessage = request.queryParams("tweetMessage");
	    	User u = (User) (request.session().attribute("user"));
//ravi changes end		
			try {
				Tweet insertTweet = new Tweet();
				insertTweet.connect();
//ravi changes start
//ravi userid related change here
				insertTweet.insert(tweetMessage, u.getUserID(), "");
//ravi changes end 				
				return "success";
			} catch (NumberFormatException ex) {
				System.out.println("bad input");
			}
			return "failure";
		};

		// USED
		private static String getreplies(spark.Request request, spark.Response response) {
			checkSession(request);
			int tweetID = Integer.parseInt(request.queryParams("tweetID"));
			System.out.println("tweetID "+tweetID);
			TweetReplies getreply = new TweetReplies();
			getreply.connect();
			ArrayList<String> replylist = new ArrayList<String>();
			replylist = getreply.getreply(tweetID); // returns tweetList
			System.out.println("replylist "+replylist);
			Gson gson = new Gson();
			return gson.toJson(replylist);
		};

		// USED
		private static String tweetLike(spark.Request request, spark.Response response) {
			checkSession(request);
	    	User u = (User) (request.session().attribute("user"));
			int tweetID = Integer.parseInt(request.queryParams("tweetID"));
			int userID = u.getUserID();
			//userID = 2; // RAVI, this needs to be removed

			try {
				Likes addLike = new Likes();
				addLike.connect();
				addLike.addLike(tweetID, userID);
				return "success";
			} catch (NumberFormatException ex) {
				System.out.println("bad input");
			}
			return "failure";
		};

	//RAVI CODE ENDS
	  public static void main(String[] args) {
	        port(3000);
	        staticFiles.location("/public");
	        
	        // show log in, auth, register.  login page is a template because it needs to handle header include
	        get("/login", (request, response) -> {
	        	return logIn(request, response);
	        });
	        post("/authenticate", (request, response) -> {
	        	return authenticate(request, response);
	        });
	        post("/register", (request, response) -> {
	        	return register(request, response);
	        });
	        get("/about", (request, response) -> {
	        	return about(request, response);
	        });
	        get("/logout", (request, response) -> {
	        	request.session().removeAttribute("user");
	        	response.redirect("/login");
	        	return "";
	        });
	        
	        // user maint pages - handle / password changes, block other users
	        get("/userUpdate", (request, response) -> {
	        	return updateUser(request, response);
	        });
	        post("/updateHandle", (request, response) -> {
	        	return updateHandle(request, response);
	        });
	        post("/updatePassword", (request, response) -> {
	        	return updatePassword(request, response);
	        });
	        
	        // may not be used - confirm
//	        get("/update", (request, response) -> {
//	        	return processUpdateUser(request, response);
//	        });
	        
	        // actions related to following (show all, follow, unfollow)
	        //TO FOLLOW:
	        // present page shell for users who are NOT followed
	        get("/userFollow", (request, response) -> {
	        	return userFollow(request, response);
	        });
	        // return the specific list of users NOT followed by the logged in User
	        get("/showUnFollowedUsers", (request, response) -> {
	        	return showUnFollowedUsers(request, response);
	        }); 
	        // handle request to follow a specific user
	        post("/followThisUser", (request, response) -> {
	        	return followThisUser(request, response);
	        });
	        
	        //TO STOP FOLLOWING:
	        // present page of users currently followed
	        get("/userStopFollowing", (request, response) -> {
	        	return userStopFollowing(request, response);
	        });
	        // return the specific list of users who ARE followed by the logged in User
	        get("/showFollowedUsers", (request, response) -> {
	        	return showFollowedUsers(request, response);
	        }); 
	        // handle request to STOP following a specific user
	        post("/stopFollowingThisUser", (request, response) -> {
	        	return stopFollowingThisUser(request, response);
	        });
	        
	        //show timelines
	        // return the specific list of users who ARE followed by the logged in User
	        get("/showTimelines", (request, response) -> {
	        	return showTimelines(request, response);
	        }); 
	        get("/getUserTimeline", (request, response) -> {
	        	return getUserTimeline(request, response);
	        });
	        
	        
	        //Showing users for Select (Dropdown)
	        // return the specific list of users who ARE followed by the logged in User
	        get("/showFollowersSelect", (request, response) -> {
	        	return showFollowersSelect(request, response);
	        }); 
	        get("/showNOTFollowersSelect", (request, response) -> {
	        	return showNOTFollowersSelect(request, response);
	        }); 
	        
	        
	        //TO BLOCK:
	        // show users currently following
	        get("/showFollowers", (request, response) -> {
	        	return showFollowers(request, response);
	        });
	        // handle request to block a specific user
	        post("/blockFollower", (request, response) -> {
	        	return blockFollower(request, response);
	        });
	        
	      //RAVI CODE STARTS
			// Post handler for storing the tweet to DB
			post("/insertTweet", (request, response) -> {
				return TwitterRouter.insertTweet(request, response);
			});

			// This is the main tweets messages..does
			// 1) provides insert tweets option and 2) gets all existing tweets
			get("/getTweetHTML", (request, response) -> {
//RAVI CHANGES START			
				String reqType = "main";
				return TwitterRouter.getTweet(request, response, reqType);
			});
//RAVI CHANGES END
			// Gets all the tweets for the user
			get("/getTweets", (request, response) -> {
				return TwitterRouter.getTweets(request, response);
			});

			// Inserts replies against the tweets into DB
			post("/tweetReply", (request, response) -> {
				return TwitterRouter.tweetReply(request, response);
			});

			// Gets all the replies for the tweetid
			get("/getreplies", (request, response) -> {
				return TwitterRouter.getreplies(request, response);
			});
//RAVI CHANGES START
			// request to get your own tweets
			get("/getOwnTweet", (request, response) -> {
				String reqType = "own";
				return TwitterRouter.getTweet(request, response, reqType);
			});
//RAVI CHANGES END
			// Inserts likes into DB
			post("/like", (request, response) -> {
				System.out.println("like post call begins");
				return TwitterRouter.tweetLike(request, response);
			});
	//RAVI CODE ENDS   
	}
}
