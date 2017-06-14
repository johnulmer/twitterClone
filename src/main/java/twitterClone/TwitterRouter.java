package twitterClone;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;
import java.util.ArrayList;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import com.google.gson.Gson;

public class TwitterRouter {

    public static void main(String[] args) {
        port(3000);
        //AlbumList albumList = new AlbumList();

//        get("/album/:id", (req, 0res) -> {
//            System.out.println("request made");
//            System.out.println(req.queryParams("b"));
//            return "hi world2";
//        });
        get("/login", (request, response) -> {
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
	        JtwigModel model = JtwigModel.newModel();
	        //JtwigModel model = JtwigModel.newModel().with("albums", albumList.albums);
	        return template.render(model);
        });
        
        get("/authenticate", (request, response) -> {
        	Gson gson = new Gson();
        	int UserID = User.Authenticate("user1", "pwd1");
        	System.out.println("found user: " + UserID);
        	return gson.toJson((Integer) UserID);
        });
        
        get("/userByID", (req, res) -> {
            Gson gson = new Gson();
            User u = User.GetUserByUserID(3);
            System.out.println(u);
            return gson.toJson(u);
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
        
//      get("/userByID", (req, res) -> {
//      Gson gson = new Gson();
//      User u = User.GetUserByUserID(3);
//      System.out.println(u);
//      return gson.toJson(u);
 
  
//  //this is for testing insert tweet method
//  get("/insertTweet2", (request, response) -> {
//  	Tweet insertTweet = new Tweet();
//  	insertTweet.connect();
//  	insertTweet.insert(6,"insert tweet testing6",6666,"");        	
//      JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
//      JtwigModel model = JtwigModel.newModel();
//      return template.render(model);
//  });
}
}
