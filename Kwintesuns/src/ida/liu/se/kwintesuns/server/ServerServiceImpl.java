package ida.liu.se.kwintesuns.server;

import java.util.ArrayList;
import java.util.Date;

import ida.liu.se.kwintesuns.client.Comment;
import ida.liu.se.kwintesuns.client.MyUser;
import ida.liu.se.kwintesuns.client.ServerService;
import ida.liu.se.kwintesuns.client.Post;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends RemoteServiceServlet implements 
ServerService {
	
	private UserService userService = UserServiceFactory.getUserService();
    private Objectify ofy = ObjectifyService.begin();

	//The Objectify service for the entity must be registered before any 
    //operations can be executed
	static {
        ObjectifyService.register(MyUser.class);
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Comment.class);
	}
	
	/**
	 * 	-------------------------------------MyUser services----------------------------------
	 */
	
	// returns the user, if he or she is logged in
	public MyUser getCurrentMyUser() {
        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
        if (user != null)
    		return makeMyUser(user);
        else
        	return null;
	}

	// add user to the database if it's not already there
	private MyUser makeMyUser(User user) {
		if (userService.isUserLoggedIn()) {
			MyUser myUser = new MyUser(user.getNickname(), userService.isUserAdmin());
			if(userIsNew(user))
				ofy.put(myUser);
			return myUser;
		} else {
			return null;
		}
	}

	// check if the user is new - if the user is new, 
	// it will not exist in the datastore
	private boolean userIsNew(User user) {
		try {
			ofy.get(MyUser.class, user.getFederatedIdentity());
		} catch (IllegalArgumentException e) {
			return true; //user not found
		}
		return false;
	}

	//add subscription and update the user in the datastore
	public void subscribe(String emailToSubscribeTo) {
		
		MyUser u = getCurrentMyUser();
		if (u != null) {
			ofy.delete(MyUser.class, u.getFederatedId()); 	//delete old user
			u.addSubscription(emailToSubscribeTo); 			//update user
			ofy.put(u); 									//add updated user
		}
	}
	
	//remove subscription and update the user in the datastore
	public void unsubscribe(String emailToUnsubscribeFrom) {
		
		MyUser u = getCurrentMyUser();
		if (u != null) {
			ofy.delete(MyUser.class, u.getFederatedId()); 	//delete old user
			u.removeSubscription(emailToUnsubscribeFrom); 	//update user
			ofy.put(u); 									//add updated user
		}
	}
		
	/**
	 * 	-------------------------------------Post services----------------------------------
	 */
	
	// store a new post in the datastore
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

	// delete a post stored in the datastore
	public void deletePost(Long postId) throws NullPointerException {
		try {
			ofy.delete(Post.class, postId);
		} catch (NullPointerException e) {
			throw e;
		}
	}

	// update a post stored in the datastore
	public void editPost(Long oldPostId, Post updatedPost) throws NullPointerException {
		if (updatedPost != null) {
			try {
				Post p = ofy.get(Post.class, oldPostId);
				deletePost(p.getId());
			} catch (NullPointerException e) {
				throw e;
			}

			ofy.put(updatedPost);
			
			// relink all comments to the updated post
			ArrayList<Comment> commentList = getComments(oldPostId);
	        for (Comment c : commentList) {
				updatePostLink(c.getCommentId(), updatedPost.getId());	        	
	        }
		}
	}

	// returns all posts
	public ArrayList<Post> getAllPosts() {		
		
		// sorts the query by date in descending order
		Iterable<com.googlecode.objectify.Key<Post>> allKeys = 
				ofy.query(Post.class).order("-date").fetchKeys();
		ArrayList<Post> posts = new ArrayList<Post>();
		
		// loop through the query result and add to the array
		Post p;
		for (com.googlecode.objectify.Key<Post> k : allKeys) {
			try {
				p = ofy.get(k);
				posts.add(p);
			} catch (NotFoundException e) {
				//this entry was probably just removed - skip it
			}
		}		
		return posts;
	}
	
	// returns all posts that fit the filter
	public ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) {	
		
		Query<Post> q = null;
		ArrayList<Post> posts = new ArrayList<Post>();
		// Loop through the list of filters and query the db using each one
		for (int i = 0; i < filter.size(); i++) {
			try {
				// sorts the query by date in descending order
				q = ofy.query(Post.class).filter(filterBy, filter.get(i)).order("-date");
				// Loop through the query results and add to the array
				for (Post fetched : q)
					posts.add(fetched);
			} catch (NullPointerException e) {
				// the query didn't find any matching posts for this filter - skip it
			}
		}
		return posts;
	}
	
	/**
	 * 	-------------------------------------Comment services----------------------------------
	 */
	
	// store a new comment in the datastore
	public void storeComment(Comment comment) {     
		
        if (comment != null) {
        	if (userService.isUserLoggedIn())
        		comment.setAuthor(userService.getCurrentUser().getNickname());
        	else
        		comment.setAuthor("Anonymous");
        	comment.setDate(new Date());
        	ofy.put(comment);
        } 
	}

	// delete a comment from the datastore
	public void deleteComment(Long commentId) {
		ofy.delete(Comment.class, commentId);
	}

	// returns all comments belonging to the post with postId
	public ArrayList<Comment> getComments(Long postId) {

		// sorts the query by date in ascending order
		Iterable<com.googlecode.objectify.Key<Comment>> allKeys = 
				ofy.query(Comment.class).filter("postId", postId).order("date").fetchKeys();
		ArrayList<Comment> comments = new ArrayList<Comment>();

		//Loop through the query results and add to the array
		for (com.googlecode.objectify.Key<Comment> k : allKeys) {
			Comment c = ofy.get(k);
			comments.add(c);
		}		
		return comments;
	}
	
	private void updatePostLink(Long commentId, Long newPostId) {
		Comment c = ofy.get(Comment.class, commentId);
		if (c != null) {
			deleteComment(c.getCommentId());
			c.setCommentId(newPostId);
			ofy.put(c);
		}
	}
}
