package twitterClone;

import static spark.Spark.*;
import java.util.ArrayList;

import javax.xml.ws.Response;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import com.google.gson.Gson;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

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
			request.session().attribute("handle", User.GetUserByUserID(UserID).getHandle());
			return "SUCCESS";
		} else {
			return "Unable to authenticate - please try again or register if you are a new user.";
		}
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
	
//	IN PROGRESS HERE 
	private static String getreplies(spark.Request request, spark.Response response) {
//		private static String getreplies(spark.Request request, spark.Response response) {
		System.out.println("begin getreplies");
		System.out.println(request.queryParams("rtweetID"));
		int rtweetID = Integer.parseInt(request.queryParams("rtweetID"));
//		int rtweetID = 1;
		TweetReplies getreply = new TweetReplies();
		getreply.connect();
		ArrayList<String> replylist = new ArrayList<String>();
//		replylist = getreply.getreply(1); // returns tweetList
		replylist = getreply.getreply(rtweetID); // returns tweetList
		System.out.println(replylist);
//		JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/getTweet.jtwig");
		// JtwigTemplate.fileTemplate("c:/");
//		JtwigModel model = JtwigModel.newModel().with("replies", replylist);
//		return replylist;
//		return template.render(model);
//		return replylist;
		Gson gson = new Gson(); 
		return gson.toJson(replylist);
	};

	public static void main(String[] args) {
		port(3000);

		get("/login", (request, response) -> {
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
			JtwigModel model = JtwigModel.newModel();
			return template.render(model);
		});

		get("/userFollow", (request, response) -> {
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userFollow.jtwig");
			JtwigModel model = JtwigModel.newModel();
			return template.render(model);
		});

		get("/showUnfollowedUsers", (request, response) -> {
			System.out.println("showing unfollowed users");
			User u = User.GetUserByUserID(2);
			ArrayList unfollowedUsers = u.UnfollowedUsers();
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userDisplay.jtwig");
			JtwigModel model = JtwigModel.newModel().with("userlist", unfollowedUsers);
			System.out.println(template.render(model));
			return template.render(model);
		});

		post("/authenticate", (request, response) -> {
			return TwitterRouter.Authenticate(request, response);
		});

		post("/register", (request, response) -> {
			return TwitterRouter.Register(request, response);
		});

		get("/userUpdate", (request, response) -> {
			// System.out.println(request.session().attributes());
			// System.out.println("userID: " +
			// request.session().attribute("userID"));
			// System.out.println("username: " +
			// request.session().attribute("username"));
			// System.out.println("handle: " +
			// request.session().attribute("handle"));
			User u = User.GetUserByUserID(Integer.parseInt(request.session().attribute("userID")));
			// User u = new User("users name", "pass word", "this is a handle");
			JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/userUpdate.jtwig");
			JtwigModel model = JtwigModel.newModel().with("user", u);
			return template.render(model);
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

		//  Inserts replies against the tweets into DB
		post("/tweetReply", (request, response) -> {
			return TwitterRouter.tweetReply(request, response);
		});
		
		// Gets all the replies for the tweetid
		get("/getreplies", (request, response) -> {
			return TwitterRouter.getreplies(request, response);
		});

		/*
		 * 
		 * Reply related handlers (DELETE LATER) get("/replyTweet", (request,
		 * response) -> { JtwigTemplate template =
		 * JtwigTemplate.classpathTemplate("templates/enterReply.jtwig");
		 * JtwigModel model = JtwigModel.newModel(); return
		 * template.render(model); });
		 * 
		 * request to get your own tweets get("/getOwnTweet", (request,
		 * response) -> { Tweet getTweet = new Tweet(); getTweet.connect();
		 * System.out.println(getTweet.get(1, "self")); JtwigTemplate template =
		 * JtwigTemplate.classpathTemplate("templates/login.jtwig"); JtwigModel
		 * model = JtwigModel.newModel(); return template.render(model); });
		 * 
		 * // request to get own tweet + followers tweet get("/getMainTweet",
		 * (request, response) -> { Tweet getTweet = new Tweet();
		 * getTweet.connect();
		 * 
		 * ArrayList<String> tweetList = new ArrayList<String>(); tweetList =
		 * getTweet.get(1, "main"); // returns tweetList
		 * System.out.println(tweetList);
		 * 
		 * Gson gson = new Gson(); return gson.toJson(tweetList);
		 */
		// });

	}
}
