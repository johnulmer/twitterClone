package twitterClone;

import static spark.Spark.*;
import java.util.ArrayList;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
//import com.google.gson.Gson;

public class TwitterRouter {
	
	private static void SetSession(spark.Request request, User u) {
        request.session().attribute("userID", u.GetUserID());
        request.session().attribute("username", u.getUserName());
        request.session().attribute("handle", u.getHandle());
	}
	private static String LogIn(spark.Request request, spark.Response response) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
        JtwigModel model = JtwigModel.newModel();
        return template.render(model);
	}
	private static String Authenticate(spark.Request request, spark.Response response) {
        User u = new User(request.queryParams("username"), 
        		request.queryParams("password"), 
        		request.queryParams("handle"));
    	u.Authenticate();
    	if (u.GetUserID() > -1) {
    		SetSession(request, u);
    		return "SUCCESS";
    	} else {
    		return "Unable to authenticate - please try again or register if you are a new user.";
    	}
	}
	private static String Register(spark.Request request, spark.Response response) {
        User u = new User(request.queryParams("username"), 
        		request.queryParams("password"), 
        		request.queryParams("handle"));
        String registration = u.ValidNewUser();
        if (registration.equals("SUCCESS")) {
        	u.Register();
        	SetSession(request, u);
        }
    	return registration;
	}
	private static String Update(spark.Request request, spark.Response response) {
    	User u = new User(Integer.parseInt(request.session().attribute("userID")));
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userUpdate.jtwig");
        JtwigModel model = JtwigModel.newModel().with("user", u);
        return template.render(model);
	}
	private static String UserFollow(spark.Request request, spark.Response response) {
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userFollow.jtwig");
	    JtwigModel model = JtwigModel.newModel();
	    return template.render(model);
	}
    
	private static String ShowFollowedUsers(spark.Request request, spark.Response response) {
    	User u = new User(2);  // hard-coded for testing, will add session checking later
    	ArrayList<User> followedUsers = u.GetFollowedUsers();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/FollowedUserDisplay.jtwig");
        JtwigModel model = JtwigModel.newModel().with("userlist", followedUsers);
        System.out.println(template.render(model));
        return template.render(model);
	}
	private static String ShowUnFollowedUsers(spark.Request request, spark.Response response) {
		User u = new User(2);  // hard-coded for testing, will add session checking later
		ArrayList<User> unfollowedUsers = u.GetUnfollowedUsers();
	    JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/UnFollowedUserDisplay.jtwig");
	    JtwigModel model = JtwigModel.newModel().with("userlist", unfollowedUsers);
	    System.out.println(template.render(model));
	    return template.render(model);
	}

    public static void main(String[] args) {
        port(3005);
        staticFiles.location("/public");
        
        // show log in, auth, register.  login page is a template to handle header include
        get("/login", (request, response) -> {
        	return LogIn(request, response);
        });
        post("/authenticate", (request, response) -> {
        	return Authenticate(request, response);
        });
        post("/register", (request, response) -> {
        	return Register(request, response);
        });
        
        // user maint pages - handle / password changes, TODO add block other users
        get("/userUpdate", (request, response) -> {
        	return Update(request, response);
        });
        
        // actions related to following (show all, follow, unfollow)
        get("/userFollow", (request, response) -> {
        	return UserFollow(request, response);
        });
        get("/showUnfollowedUsers", (request, response) -> {
        	return ShowUnFollowedUsers(request, response);
        });   
        get("/showFollowedUsers", (request, response) -> {
        	return ShowFollowedUsers(request, response);
        }); 
        
        //this is for inserting tweet into DB. 
        get("/insertTweet", (request, response) -> {
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/enterTweet.jtwig");
	        JtwigModel model = JtwigModel.newModel();
	        return template.render(model);
        });
        
        post("/insertTweet", (req, res) -> {
        	System.out.print("/insertTweet post req 1 ");
            String first = req.queryParams("tweetMessage");
            System.out.print("/insertTweet post req 2 ");
            try {
            	Tweet insertTweet = new Tweet();
            	insertTweet.connect();
            	insertTweet.insert(12,first,1,"");     
            	System.out.print("/insertTweet post req 3 ");
                return "success";
            } catch (NumberFormatException ex) {
                System.out.println("bad input");
            }
            System.out.print("/insertTweet post req 4 ");
            return "failure";
        });
        
        //request to get your own tweets
        get("/getOwnTweet", (request, response) -> {
        	Tweet getTweet = new Tweet();
        	getTweet.connect();
        	System.out.println(getTweet.get(1,"self"));  
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
