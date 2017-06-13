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

//        get("/album/:id", (req, res) -> {
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
