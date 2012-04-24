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

	// The Objectify service for the entity must be registered before any 
    // operations can be executed
	static {
        ObjectifyService.register(MyUser.class);
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Comment.class);
	}
	
	/**
	 * 	-------------------------------------MyUser services----------------------------------
	 */
	
	// Returns the user, if he or she is logged in
	public MyUser getCurrentMyUser() {
		
        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
        if (user != null)
    		return makeMyUser(user);
        else
        	return null;
	}

	// Add user to the database if it's not already there
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

	// Check if the user is new - if the user is new, 
	// it will not exist in the datastore
	private boolean userIsNew(User user) {
		
		try {
			ofy.get(MyUser.class, user.getFederatedIdentity());
		} catch (IllegalArgumentException e) {
			return true; //user not found
		}
		return false;
	}

	// Add subscription and update the user in the datastore
	public void subscribe(String emailToSubscribeTo) {
		
		MyUser u = getCurrentMyUser();
		if (u != null) {
			ofy.delete(MyUser.class, u.getFederatedId()); 	//delete old user
			u.addSubscription(emailToSubscribeTo); 			//update user
			ofy.put(u); 									//add updated user
		}
	}
	
	// Remove subscription and update the user in the datastore
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
	
	// Store a new post in the datastore
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

	// Delete a post stored in the datastore
	public void deletePost(Long postId) throws NotFoundException {
		
		Post p;
		try {
			p = ofy.get(Post.class, postId);
		} catch (NotFoundException e) {
			throw e;
		}
		ofy.delete(p);
		
		// Delete all comments linked to the deleted post
		ArrayList<Comment> commentList = getComments(postId);
        for (Comment c : commentList) {
        	deleteComment(c.getId());
        }
	}

	// Update a post stored in the datastore
	public void editPost(Long oldPostId, Post updatedPost) throws NotFoundException {
		
		if (updatedPost != null) {
			Post p;
			try {
				p = ofy.get(Post.class, oldPostId);
			} catch (NotFoundException e) {
				// Don't continue if the old post wasn't found 
				throw e;
			}
			deletePost(p.getId());

			ofy.put(updatedPost);
			
			// Re-link all comments to the updated post
			ArrayList<Comment> commentList = getComments(oldPostId);
	        for (Comment c : commentList) {
				updatePostLink(c.getId(), updatedPost.getId());
	        }
		}
	}

	// Returns all posts
	public ArrayList<Post> getAllPosts() {		
		
		// sorts the query by date in descending order
		Iterable<com.googlecode.objectify.Key<Post>> allKeys = 
				ofy.query(Post.class).order("-date").fetchKeys();
		ArrayList<Post> posts = new ArrayList<Post>();
		Post p;
		
		// loop through the query result and add to the array
		for (com.googlecode.objectify.Key<Post> k : allKeys) {
			try {
				p = ofy.get(k);
			} catch (NotFoundException e) {
				//this entry was probably recently removed - skip it
				continue;
			}
			posts.add(p);
		}		
		return posts;
	}
	
	// Returns all posts that fit the filter
	public ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) {	
		
		Query<Post> q = null;
		ArrayList<Post> posts = new ArrayList<Post>();
		// Loop through the list of filters and query the db using each one
		for (int i = 0; i < filter.size(); i++) {
			try {
				// sorts the query by date in descending order
				q = ofy.query(Post.class).filter(filterBy, filter.get(i)).order("-date");
			} catch (NullPointerException e) {
				// the query didn't find any matching posts for this filter - skip it
				continue;
			}
			// Loop through the query results and add to the array
			for (Post fetched : q)
				posts.add(fetched);
		}
		return posts;
	}
	
	/**
	 * 	-------------------------------------Comment services----------------------------------
	 */
	
	// Store a new comment in the datastore
	public void storeComment(String text, Long postId) {
		
		Post oldPost;
		// Check if the comment is linked to a existing post
    	try {
	    	oldPost = ofy.get(Post.class, postId);
    	} catch (NotFoundException e) {
    		// If the comment isn't linked to any existing post
    		// don't continue
    		return;
    	}
    	
    	// Update the date of the post which the comment belongs to 
    	// to match the latest activity
		Post updatedPost = oldPost;
    	updatedPost.setDate(new Date());
    	editPost(postId, updatedPost);
    	
    	// Check if the edit worked
    	try {
	    	updatedPost = ofy.get(Post.class, postId);
    	} catch (NotFoundException e) {
    		// Don't continue if the edit failed
    		return;
    	}
    	
		Comment comment = new Comment(text, postId);
    	comment.setDate(updatedPost.getDate());    	
    	if (userService.isUserLoggedIn())
    		comment.setAuthor(userService.getCurrentUser().getNickname());
    	else
    		comment.setAuthor("Anonymous");
    	ofy.put(comment);
	}

	// Delete a comment from the datastore
	public void deleteComment(Long commentId) throws NotFoundException {
		Comment c;
		try {
			c = ofy.get(Comment.class, commentId);
		} catch (NotFoundException e) {
			throw e;
		}
		ofy.delete(c);
	}

	// Returns all comments belonging to the post with postId
	public ArrayList<Comment> getComments(Long postId) {

		// sorts the query by date in descending order
		Iterable<com.googlecode.objectify.Key<Comment>> allKeys = 
				ofy.query(Comment.class).filter("postId", postId).order("-date").fetchKeys();
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Comment c;

		//Loop through the query results and add to the array
		for (com.googlecode.objectify.Key<Comment> k : allKeys) {
			try {
				c = ofy.get(k);
			} catch (NotFoundException e) {
				//this entry was probably recently removed - skip it
				continue;
			}
			comments.add(c);
		}		
		return comments;
	}
	
	// Make sure that the comments are linked to the correct post
	private void updatePostLink(Long commentId, Long newPostId) {
		Comment c;
		try {
			c = ofy.get(Comment.class, commentId);
		} catch (NotFoundException e) {
			// Don't continue if a comment with that id wasn't found
			return;
		}
		ofy.delete(c);
		c.setPostId(newPostId);
		ofy.put(c);
	}
}