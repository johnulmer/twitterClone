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
        request.session().attribute("userID", u.getUserID());
        request.session().attribute("username", u.getUserName());
        request.session().attribute("handle", u.getHandle());
	}
	private static String logIn(Request request, Response response) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
        JtwigModel model = JtwigModel.newModel();
        return template.render(model);
	}
	private static String authenticate(Request request, Response response) {
        User u = new User(request.queryParams("username"), 
        		request.queryParams("password"), 
        		request.queryParams("handle"));
    	u.authenticate();
    	if (u.getUserID() > -1) {
    		setSession(request, u);
    		return "SUCCESS";
    	} else {
    		return "Unable to authenticate - please try again or register if you are a new user.";
    	}
	}
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
	private static String updateUser(Request request, Response response) {
    	User u = new User(Integer.parseInt(request.session().attribute("userID")));
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userUpdate.jtwig");
        JtwigModel model = JtwigModel.newModel().with("user", u);
        return template.render(model);
	}
	private static String userFollow(Request request, Response response) {
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userFollow.jtwig");
	    JtwigModel model = JtwigModel.newModel();
	    return template.render(model);
	}
    
	private static String showFollowedUsers(Request request, Response response) {
    	User u = new User(2);  // hard-coded for testing, will add session checking later
    	ArrayList<User> followedUsers = u.getFollowedUsers();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/FollowedUserDisplay.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        System.out.println(template.render(model));
        return template.render(model);
	}
	private static String showUnFollowedUsers(Request request, Response response) {
		User u = new User(2);  // hard-coded for testing, will add session checking later
		ArrayList<User> unfollowedUsers = u.getUnfollowedUsers();
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/UnFollowedUserDisplay.jtwig");
	    JtwigModel model = JtwigModel.newModel().with("userlist", unfollowedUsers);
	    System.out.println(template.render(model));
	    return template.render(model);
	}

  

	private static String tweetReply(spark.Request request, spark.Response response) {
		int vtweetID = Integer.parseInt(request.queryParams("vtweetID"));
		int vuserID = Integer.parseInt(request.queryParams("vuserID"));
		vuserID = 2; // RAVI, this needs to be removed
		String vtweetReply = request.queryParams("vtweetReply");
		try {
			Tweet insertReply = new Tweet();
			insertReply.connect();
			insertReply.insertReply(vtweetID, vuserID, vtweetReply);
			return "success";
		} catch (NumberFormatException ex) {
			System.out.println("bad input");
		}
		return "failure";
	};

	private static String getTweet(spark.Request request, spark.Response response) {
		System.out.println("begin getMainHTML");
		Tweet getTweet = new Tweet();
		getTweet.connect();
		ArrayList<String> tweetList = new ArrayList<String>();
		tweetList = getTweet.get(1, "main"); // returns tweetList
		System.out.println(tweetList);
		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/getTweet.jtwig");
		// JtwigTemplate.fileTemplate("c:/");
		JtwigModel model = JtwigModel.newModel().with("tweets", tweetList);
		return template.render(model);
	};

	private static String insertTweet(spark.Request request, spark.Response response) {
		System.out.print("/insertTweet post req 1 ");
		String first = request.queryParams("tweetMessage");
		System.out.print("/insertTweet post req 2 ");
		try {
			Tweet insertTweet = new Tweet();
			insertTweet.connect();
			insertTweet.insert(12, first, 1, "");
			System.out.print("/insertTweet post req 3 ");
			return "success";
		} catch (NumberFormatException ex) {
			System.out.println("bad input");
		}
		System.out.print("/insertTweet post req 4 ");
		return "failure";
	};

	// IN PROGRESS HERE
	private static String getreplies(spark.Request request, spark.Response response) {
		// private static String getreplies(spark.Request request,
		// spark.Response response) {
		System.out.println("begin getreplies");
		System.out.println(request.queryParams("rtweetID"));
		int rtweetID = Integer.parseInt(request.queryParams("rtweetID"));
		// int rtweetID = 1;
		TweetReplies getreply = new TweetReplies();
		getreply.connect();
		ArrayList<String> replylist = new ArrayList<String>();
		// replylist = getreply.getreply(1); // returns tweetList
		replylist = getreply.getreply(rtweetID); // returns tweetList
		System.out.println(replylist);
		// JtwigTemplate template =
		// JtwigTemplate.classpathTemplate("templates/getTweet.jtwig");
		// JtwigTemplate.fileTemplate("c:/");
		// JtwigModel model = JtwigModel.newModel().with("replies", replylist);
		// return replylist;
		// return template.render(model);
		// return replylist;
		Gson gson = new Gson();
		return gson.toJson(replylist);
	};

	  public static void main(String[] args) {
	        port(3007);
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
	        
	        // user maint pages - handle / password changes, TODO add block other users
	        get("/userUpdate", (request, response) -> {
	        	return updateUser(request, response);
	        });
	        
	        // actions related to following (show all, follow, unfollow)
	        get("/userFollow", (request, response) -> {
	        	return userFollow(request, response);
	        });
	        get("/showUnFollowedUsers", (request, response) -> {
	        	return showUnFollowedUsers(request, response);
	        });   
	        get("/showFollowedUsers", (request, response) -> {
	        	return showFollowedUsers(request, response);
	        }); 
	        
	        
			// get handler for getting the tweets for the user
			get("/insertTweet", (request, response) -> {
				JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/enterTweet.jtwig");
				JtwigModel model = JtwigModel.newModel();
				return template.render(model);
			});


			// Post handler for storing the tweet to DB
			post("/insertTweet", (request, response) -> {
				return TwitterRouter.insertTweet(request, response);
			});

			// Gets all the tweets for the user
			get("/getTweetHTML", (request, response) -> {
				return TwitterRouter.getTweet(request, response);
			});

			// Inserts replies against the tweets into DB
			post("/tweetReply", (request, response) -> {
				return TwitterRouter.tweetReply(request, response);
			});

			// Gets all the replies for the tweetid
			get("/getreplies", (request, response) -> {
				return TwitterRouter.getreplies(request, response);
			});

			// request to get your own tweets
			get("/getOwnTweet", (request, response) -> {
				Tweet getTweet = new Tweet();
				getTweet.connect();
				System.out.println(getTweet.get(1, "self"));
				JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
				JtwigModel model = JtwigModel.newModel();
				return template.render(model);
			});    
	        
	        //request to get own tweet + followers tweet        	
	        get("/getMainTweet", (request, response) -> {
	        	Tweet getTweet = new Tweet();
	        	getTweet.connect();
	        	System.out.println(getTweet.get(1,"main"));
		        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
		        JtwigModel model = JtwigModel.newModel();
		        return template.render(model);
	        });		
			        
	}
}
