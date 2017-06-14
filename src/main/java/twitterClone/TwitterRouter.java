package twitterClone;




import static spark.Spark.*;
import java.util.ArrayList;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import com.google.gson.Gson;

public class TwitterRouter {
	
	private static String Register(spark.Request request, spark.Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        String handle = request.queryParams("handle");
        String registration = User.Register(username, password, handle);
        if (registration.equals("SUCCESS")) {
	        request.session().attribute("userID", User.GetUserByUserName(username));
	        request.session().attribute("username", username);
	        request.session().attribute("handle", handle);
        }
    	return registration;
	}
	
	private static String Authenticate(spark.Request request, spark.Response response) {
        String username = request.queryParams("username");
        String password = request.queryParams("password");
    	int UserID = User.Authenticate(username, password);
    	if (UserID > -1) {
	        request.session().attribute("userID", Integer.toString(UserID));
	        request.session().attribute("username", username);
    		return "SUCCESS";
    	} else {
    		return "Unable to authenticate - please try again or register if you are a new user.";
    	}
	}

    public static void main(String[] args) {
        port(3003);

        get("/login", (request, response) -> {
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
	        JtwigModel model = JtwigModel.newModel();
	        return template.render(model);
        });        
 
        post("/authenticate", (request, response) -> {
        	return TwitterRouter.Authenticate(request, response);
        });
        
        post("/register", (request, response) -> {
        	return TwitterRouter.Register(request, response);
        });
        
        get("/userUpdate", (request, response) -> {
        	User u = new User("users name", "pass word", "this is a handle");
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userUpdate.jtwig");
	        JtwigModel model = JtwigModel.newModel().with("user", u);
	        return template.render(model);
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
