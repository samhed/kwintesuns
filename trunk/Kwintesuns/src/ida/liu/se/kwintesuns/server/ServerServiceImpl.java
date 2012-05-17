package ida.liu.se.kwintesuns.server;

import java.util.ArrayList;
import java.util.Date;

import ida.liu.se.kwintesuns.client.Comment;
import ida.liu.se.kwintesuns.client.MyUser;
import ida.liu.se.kwintesuns.client.ServerService;
import ida.liu.se.kwintesuns.client.Post;

import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
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
	/*public MyUser getCurrentMyUser() {
		
        User user = userService.getCurrentUser(); // or req.getUserPrincipal()
        if (user != null)
    		return makeMyUser(user);
        else
        	return null;
	}*/
	
	public MyUser getCurrentMyUser() {
		MyUser myUser;
		User user = userService.getCurrentUser();
		if (user != null) {				
			try {
				myUser = ofy.get(MyUser.class, user.getEmail());
			} catch (IllegalArgumentException e) {			
				return null; //name cannot be null or empty
			} catch (NotFoundException e) {
				// user not found in DB
				return makeMyUser(user);
			}
			
			return myUser;
		} else {
			// no user logged in
			return null;
		}
	}

	// Add user to the database if it's not already there
	private MyUser makeMyUser(User user) {
		
		if (userService.isUserLoggedIn()) {
			MyUser myUser = new MyUser(user.getEmail(),
					userService.isUserAdmin());
			ofy.put(myUser);
			return myUser;
		} else {
			return null;
		}
	}

	// Add subscription and update the user in the datastore
	public void subscribe(String emailToSubscribeTo) {
		
		MyUser u = getCurrentMyUser();
		
		if (u != null) {
			ofy.delete(MyUser.class, u.getEmail()); 	//delete old user
			u.addSubscription(emailToSubscribeTo); 			//update user
			ofy.put(u); 									//add updated user
		}
	}
	
	// Remove subscription and update the user in the datastore
	public void unsubscribe(String emailToUnsubscribeFrom) {
		
		MyUser u = getCurrentMyUser();
		
		if (u != null) {
			ofy.delete(MyUser.class, u.getEmail()); 	//delete old user
			u.removeSubscription(emailToUnsubscribeFrom); 	//update user
			ofy.put(u); 									//add updated user
		}
	}
		
	/**
	 * 	-------------------------------------Post services----------------------------------
	 */
	
	// Store a new post in the datastore
	public Long storePost(Post post) {
		
        if (post != null) {
        	if (userService.isUserLoggedIn())
        		post.setAuthor(userService.getCurrentUser().getEmail());
        	else
        		post.setAuthor("Anonymous");
    		post.setDate(new Date());
    		try {
    			return ofy.put(post).getId();
    		} catch (NotFoundException e) {
    			return null;
    		}
        }
        return null;
	}

	// Delete a post stored in the datastore
	public void deletePost(Long postId) throws NotFoundException {
		
		Post p = null;
		try {
			p = ofy.get(Post.class, postId);
		} catch (NotFoundException e) {
			throw e;
		}
		ofy.delete(p);
		
		// Delete all comments linked to the deleted post
		ArrayList<Comment> commentList = getComments(p.getId());
        for (Comment c : commentList) {
        	deleteComment(c.getId());
        }
	}

	// Update a post stored in the datastore
	public Long editPost(Long oldPostId, Post updatedPost) throws NotFoundException {
		Post oldPost;
		try {
			oldPost = ofy.get(Post.class, oldPostId);
		} catch (NotFoundException e) {
			// Don't continue if the old post wasn't found 
			throw e;
		}

		// Store the new post in the datastore
		Key<Post> updatedKey = ofy.put(updatedPost);
		// When being stored in the datastore it will get its id
		try {
			updatedPost = ofy.get(updatedKey);
		} catch (NotFoundException e) {
			// Don't continue if the updated post wasn't found 
			throw e;
		}
		
		// Re-link all comments to the updated post
		ArrayList<Comment> commentList = getComments(oldPost.getId());
        for (Comment c : commentList) {
			updatePostLink(c.getId(), updatedPost.getId());
        }
        
        deletePost(oldPost.getId());
        
        return updatedPost.getId(); 
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
			} catch (DatastoreNeedIndexException e) {
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
	public Long storeComment(String text, Long postId) {
		
		Post oldPost;
		// Check if the comment is linked to a existing post
    	try {
	    	oldPost = ofy.get(Post.class, postId);
    	} catch (NotFoundException e) {
    		// If the comment isn't linked to any existing post
    		// don't continue
    		return null;
    	}
    	
    	// Update the date of the post which the comment belongs to
    	// to match the latest activity
		Post updatedPost = new Post(oldPost.getTitle(),
				oldPost.getType(), oldPost.getDescription(), 
				oldPost.getPicture(), oldPost.getText());
    	updatedPost.setDate(new Date());
    	updatedPost.setAuthor(oldPost.getAuthor());
    	Long updatedPostId; 
    	try {
    		updatedPostId = editPost(postId, updatedPost);
    	} catch (NotFoundException e) {
    		// Don't continue if the edit failed
    		return null;
    	}
    	
		Comment comment = new Comment(text, updatedPostId);
    	comment.setDate(updatedPost.getDate());
    	if (userService.isUserLoggedIn())
    		comment.setAuthor(userService.getCurrentUser().getEmail());
    	else
    		comment.setAuthor("Anonymous");
    	ofy.put(comment);
    	return updatedPostId;
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
		Comment oldComment;
		try {
			oldComment = ofy.get(Comment.class, commentId);
		} catch (NotFoundException e) {
			// Don't continue if a comment with that id wasn't found
			return;
		}
		Comment newComment = 
				new Comment(oldComment.getText(), newPostId);
		newComment.setAuthor(oldComment.getAuthor());
		newComment.setDate(oldComment.getDate());
		ofy.delete(oldComment);
		ofy.put(newComment);
	}
}
