package twitterClone;

import static spark.Spark.get;
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
        
//        get("/userByID", (req, res) -> {
//            Gson gson = new Gson();
//            User u = User.GetUserByUserID(3);
//            System.out.println(u);
//            return gson.toJson(u);
//        });
        
        get("/insertTweet", (request, response) -> {
        	Tweet insertTweet = new Tweet();
        	insertTweet.connect();
        	insertTweet.insert(6,"insert tweet testing6",6666,"");        	
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
	        JtwigModel model = JtwigModel.newModel();
	        return template.render(model);
        });
        
        
        get("/getTweet", (request, response) -> {
        	Tweet getTweet = new Tweet();
        	getTweet.connect();
        	System.out.println(getTweet.get(1));
	        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.jtwig");
	        JtwigModel model = JtwigModel.newModel();
	        return template.render(model);
        });
        
/*        
        
        get("/album/:id", (request, response) -> {
            Album album = albumList.getAlbum(Integer.parseInt(request.params(":id")));
            if (album == null){
                return "Album was not found.";
            }
            return ("Hello: "+album.title+" "+album.artist+" "+album.genre);
        });

        get("/addAlbum/:title/:artist/:genre", (request, response) -> {
            int newID = albumList.addAlbum(request.params(":title"), request.params(":artist"), request.params(":genre"));
            return (newID);
        });
        

        
        get("/gsonAlbums", (req, res) -> {
            Gson gson = new Gson();
            System.out.println(gson.toJson(albumList.albums));
            return gson.toJson(albumList.albums);
        });*/
}
}
