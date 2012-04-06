package ida.liu.se.kwintesuns.server;

import java.util.ArrayList;
import java.util.Date;

import ida.liu.se.kwintesuns.client.Comment;
import ida.liu.se.kwintesuns.client.MyUser;
import ida.liu.se.kwintesuns.client.MyUserService;
import ida.liu.se.kwintesuns.client.Post;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MyUserServiceImpl extends RemoteServiceServlet implements 
MyUserService {
	
	private UserService userService = UserServiceFactory.getUserService();
    private Objectify ofy = ObjectifyService.begin();

	//The Objectify service for the entity must be registered before any 
    //operations can be executed
	static {
        ObjectifyService.register(MyUser.class);
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Comment.class);
	}
	
	public MyUser getCurrentMyUser() {
        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
        if (user != null)
    		return makeMyUser(user);
        else
        	return null;
	}
		
	private MyUser makeMyUser(User user) {
		if (userService.isUserLoggedIn()) {
			MyUser myUser = new MyUser(user.getNickname());
			if(userIsNotInDb(user))
				ofy.put(myUser);
			return myUser;
		} else {
			return null;
		}
	}
	
	private boolean userIsNotInDb(User user) {
		try {
			ofy.get(MyUser.class, user.getFederatedIdentity());
		} catch (Exception e) {
			return true; //user not found
		}
		return false;
	}
	
	public void storePost(Post post) {
		        
        if (post != null) {
        	if (userService.isUserLoggedIn())
        		post.setPoster(userService.getCurrentUser().getNickname());
        	else
        		post.setPoster("Anonymous");
    		post.setDate(new Date());
        	ofy.put(post);
        } 
	}
	
	public ArrayList<Post> getAllPosts() {
		
		Iterable<com.googlecode.objectify.Key<Post>> allKeys = 
				ofy.query(Post.class).order("date").fetchKeys();
		ArrayList<Post> posts = new ArrayList<Post>();
		
		for (com.googlecode.objectify.Key<Post> k : allKeys) {
			Post p = ofy.get(k);
			posts.add(p);
		}
		
		return posts;
	}
	
	public ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) {
		
		Query<Post> q = null;
		ArrayList<Post> posts = new ArrayList<Post>();
		try {
			for (int i = 0; i < filter.size(); i++) {
					q = ofy.query(Post.class).filter(filterBy, filter.get(i)).order("date");
					//Loop the query results and add to the array
					for (Post fetched : q)
						posts.add(fetched);
			}
		} catch (Exception e) {
		}
		return posts;
	}
	
	public void storeComment(Comment comment) {
        
        if (comment != null) {
        	if (userService.isUserLoggedIn())
        		comment.setPoster(userService.getCurrentUser().getNickname());
        	else
        		comment.setPoster("Anonymous");
        	comment.setDate(new Date());
        	ofy.put(comment);
        } 
	}

	public ArrayList<Comment> getComments(Long postId) {
		
		Iterable<com.googlecode.objectify.Key<Comment>> allKeys = 
				ofy.query(Comment.class).filter("postId", postId).order("date").fetchKeys();
		ArrayList<Comment> comments = new ArrayList<Comment>();
		
		for (com.googlecode.objectify.Key<Comment> k : allKeys) {
			Comment c = ofy.get(k);
			comments.add(c);
		}
		
		return comments;
	}	
}
