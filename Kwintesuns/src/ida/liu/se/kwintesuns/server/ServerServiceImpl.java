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
	private final int POSTLIMIT = 10;
	private final int COMMENTLIMIT = 10;
	private final int FLAGLIMIT = 5;

	// The Objectify service for the entity must be registered before any 
    // operations can be executed
	static {
        ObjectifyService.register(MyUser.class);
		ObjectifyService.register(Post.class);
		ObjectifyService.register(Comment.class);
	}
	
	/***************************************************************************************
	*									MyUser services
	***************************************************************************************/

	/**
	 * Gets the user, if he or she is logged in.
	 * @return the user.
	 */
	public MyUser getCurrentMyUser() {
		MyUser myUser;
		User user = userService.getCurrentUser();
		if (user != null) {
			try {
				myUser = ofy.get(MyUser.class, user.getEmail());
			} catch (IllegalArgumentException e) {
				return null; // Name cannot be null or empty
			} catch (NotFoundException e) {
				return makeMyUser(); // User not found in DB
			}			
			return myUser;
		} else {
			return null; // No user logged in
		}
	}

	/**
	 * Make a new MyUser and add it to the datastore
	 * if the user is logged in.
	 * @return the MyUser object if successful
	 */
	private MyUser makeMyUser() {
		if (userService.isUserLoggedIn()) {
			MyUser myUser = new MyUser(userService.getCurrentUser().getEmail(), userService.isUserAdmin());
			ofy.put(myUser);
			return myUser;
		} else {
			return null;
		}
	}
	
	/**
	 * Add a subscription and update the user in the datastore.
	 * @param emailToSubscribeTo
	 */
	public void subscribe(String emailToSubscribeTo) {
		MyUser u = getCurrentMyUser();		
		if (u != null) {
			u.addSubscription(emailToSubscribeTo);
			ofy.put(u);
		}
	}
	
	/**
	 * Remove the subscription and update the user in the datastore.
	 * @param emailToUnsubscribeFrom
	 */
	public void unsubscribe(String emailToUnsubscribeFrom) {
		MyUser u = getCurrentMyUser();		
		if (u != null) {
			u.removeSubscription(emailToUnsubscribeFrom);
			ofy.put(u);
		}
	}
	
	/***************************************************************************************
	*									Post services
	***************************************************************************************/
	
	/**
	 * Store a new post in the datastore.
	 * @param post needed to be able to set author and date
	 */
	public Long storePost(Post post) {
        if (post != null) {
        	if (userService.isUserLoggedIn())
        		post.setAuthor(userService.getCurrentUser().getEmail());
        	else
        		post.setAuthor("Anonymous");
        	
    		post.setDate(new Date());
    		try {
    			checkPostLimit();
    			return ofy.put(post).getId();
    		} catch (NotFoundException e) {
    			return null; // getId failed
    		}
        } else {
        	return null;
        }
	}

	/**
	 * Check if the number of posts have reached its limit.
	 * Remove the oldest posts until the number of posts is
	 * within the limit again.
	 */
	private void checkPostLimit() {
		Query<Post> q = ofy.query(Post.class).order("date");
		if (q.count() >= POSTLIMIT) {
			Iterable<com.googlecode.objectify.Key<Post>> allKeys = q.fetchKeys();
			Post p;
			
			int numberOfPostsToRemove = q.count() - POSTLIMIT;
			// Loop through the query result and remove the first one
			for (com.googlecode.objectify.Key<Post> k : allKeys) {
				try {
					p = ofy.get(k);
					deletePost(p.getId());
				} catch (NotFoundException e) {
					// This entry was probably recently removed -> skip it
					continue;
				}
				numberOfPostsToRemove--;
				if (numberOfPostsToRemove <= 0)
					break;
			}
		}
	}

	/**
	 * Delete a post, and all the comments linked to
	 * that post, from the datastore.
	 * @param postId the id of the post to be removed.
	 * @throws NotFoundException the post to be removed was not found.
	 */
	public void deletePost(Long postId) throws NotFoundException {
		Post p = null;
		// Check if the post can be found.
		try {
			p = ofy.get(Post.class, postId);
		} catch (NotFoundException e) {
			throw e; // Maybe someone was faster than you?
		}
		ofy.delete(p);
		
		// Delete all comments linked to the deleted post
		ArrayList<Comment> commentList = getComments(p.getId());
        for (Comment c : commentList)
        	deleteComment(c.getId());
	}

	/**
	 * Edit a post stored in the datastore.
	 * @param oldPostId the id of the post to be edited.
	 * @param updatedPost contents of the edited post.
	 * @throws NotFoundException the post to be edited was not found.
	 */
	public void editPost(Long oldPostId, Post updatedPost) throws NotFoundException {
		Post oldPost;
		// Get the old post.
		try {
			oldPost = ofy.get(Post.class, oldPostId);
		} catch (NotFoundException e) {
			throw e; // Don't continue if the old post wasn't found 
		}

		// Store the new post in the datastore.
		updatedPost.setId(oldPost.getId());
		ofy.put(updatedPost);
	}
	
	/**
	 * Flag a post and update it in the database.
	 * @param postId the id of the post that will get flagged.
	 * @param flagger the email of the user who is flagging this post.
	 */
	public boolean flagPost(Long postId, String flagger) {
		Post p;
		// Get the post.
		try {
			p = ofy.get(Post.class, postId);
		} catch (NotFoundException e) {			
			return false; // Don't continue if the post wasn't found
		}
		
		// Check if this user can flag the post.
		if (p.getFlagList().contains(flagger))
			return false; // The user already flagged this post
		else
			p.addToFlagList(flagger);

		// Delete the post if the size of the flaglist has 
		// reached the limit.
		if (p.getFlagList().size() >= FLAGLIMIT)
			deletePost(p.getId());	
		else
			ofy.put(p);
		return true;
	}

	/**
	 * Queries the datastore for all the posts and
	 * sort them by date.
	 * @return all the posts
	 */
	public ArrayList<Post> getAllPosts() {
		
		// Sorts the query by date in descending order
		Iterable<com.googlecode.objectify.Key<Post>> allKeys = 
				ofy.query(Post.class).order("-date").fetchKeys();
		ArrayList<Post> posts = new ArrayList<Post>();
		Post p;
		
		// Loop through the query result and add to the array
		for (com.googlecode.objectify.Key<Post> k : allKeys) {
			try {
				p = ofy.get(k);
			} catch (NotFoundException e) {
				// This entry was probably recently removed -> skip it
				continue;
			}
			posts.add(p);
		}
		return posts;
	}
	
	/**
	 * Queries the datastore for all the posts matching 
	 * the filter and sort the queries by date.
	 * @param filterBy which field to filter by
	 * @param filter an array containing all the filters
	 * @return all the posts matching the filter.
	 */
	public ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) {
		Query<Post> q = null;
		ArrayList<Post> posts = new ArrayList<Post>();
		// Loop through the list of filters and query the datastore using each one
		for (int i = 0; i < filter.size(); i++) {
			try {
				// Sorts the query by date in descending order
				q = ofy.query(Post.class).filter(filterBy, filter.get(i)).order("-date");
			} catch (DatastoreNeedIndexException e) {
				// The query didn't find any matching posts for this filter -> skip it
				continue;
			}
			// Loop through the query results and add to the array
			for (Post fetched : q)
				posts.add(fetched);
		}
		return posts;
	}
	
	/***************************************************************************************
	*									Comment services
	***************************************************************************************/
	
	/**
	 * Store a new comment in the datastore.
	 * @param text of the comment
	 * @param postId the id which the comment belongs to
	 */
	public void storeComment(String text, Long postId) {
		
		Post oldPost;
		// Get the post which the comment is going to be linked to.
    	try {
	    	oldPost = ofy.get(Post.class, postId);
    	} catch (NotFoundException e) {
    		return; // Don't continue.
    	}
    	
    	// Update the date of the post which the comment 
    	// belongs to to match the latest activity
		Post updatedPost = new Post(oldPost.getTitle(),
				oldPost.getType(), oldPost.getDescription(), 
				oldPost.getThumbnail(), oldPost.getText());
    	updatedPost.setDate(new Date());
    	updatedPost.setAuthor(oldPost.getAuthor());
    	updatedPost.setFlagList(oldPost.getFlagList());
    	try {
    		editPost(postId, updatedPost);
    	} catch (NotFoundException e) {
    		return; // Don't continue if the edit failed
    	}
    	
    	// Create the new comment.
		Comment comment = new Comment(text, postId);
    	comment.setDate(updatedPost.getDate());
    	if (userService.isUserLoggedIn())
    		comment.setAuthor(userService.getCurrentUser().getEmail());
    	else
    		comment.setAuthor("Anonymous");
    	
    	checkCommentLimit(comment.getPostId());
    	ofy.put(comment);
	}

	/**
	 * Check if the number of comments have reached its limit.
	 * Remove the oldest comments until the number of comments 
	 * is within the limit again.
	 * @param postId the id of the post which the comment is linked to
	 */
	private void checkCommentLimit(long postId) {
		Query<Comment> q =
				ofy.query(Comment.class).filter("postId", postId).order("date");
		if (q.count() >= COMMENTLIMIT) {
			Iterable<com.googlecode.objectify.Key<Comment>> allKeys = q.fetchKeys();
			Comment c;
			
			int numberOfCommentsToRemove = q.count() - COMMENTLIMIT;
			// Loop through the query result and remove the first one
			for (com.googlecode.objectify.Key<Comment> k : allKeys) {
				try {
					c = ofy.get(k);
					deleteComment(c.getId());
				} catch (NotFoundException e) {
					// This entry was probably recently removed -> skip it
					continue;
				}
				numberOfCommentsToRemove--;
				if (numberOfCommentsToRemove <= 0)
					break;
			}
		}
	}

	/**
	 * Delete a comment from the datastore.
	 * @param commentId the id of the comment to be removed
	 * @throws NotFoundException the comment to be removed was not found.
	 */
	public void deleteComment(Long commentId) throws NotFoundException {
		Comment c;
		// Check if the comment can be found.
		try {
			c = ofy.get(Comment.class, commentId);
		} catch (NotFoundException e) {
			throw e; // Maybe someone was faster than you?
		}
		ofy.delete(c);
	}

	/**
	 * Returns all comments belonging to the post with postId
	 * @param postId the id of the post
	 * @return all the comments to that post
	 */
	public ArrayList<Comment> getComments(Long postId) {
		// Sorts the query by date in descending order
		Iterable<com.googlecode.objectify.Key<Comment>> allKeys = 
				ofy.query(Comment.class).filter("postId", postId).order("-date").fetchKeys();
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Comment c;

		// Loop through the query results and add to the array
		for (com.googlecode.objectify.Key<Comment> k : allKeys) {
			try {
				c = ofy.get(k);
			} catch (NotFoundException e) {
				continue; // This entry was probably recently removed -> skip it
			}
			comments.add(c);
		}
		return comments;
	}

	/**
	 * Flag a comment and update it in the database.
	 * @param commentId the id of the comment to be flagged
	 * @param flagger the email of the user who is flagging this comment
	 */
	public boolean flagComment(Long commentId, String flagger) {
		Comment c;
		// Get the comment.
		try {
			c = ofy.get(Comment.class, commentId);
		} catch (NotFoundException e) {
			return false; // Don't continue if the comment wasn't found
		}
		
		if (c.getFlagList().contains(flagger))
			return false; // The user already flagged this comment
		else
			c.addToFlagList(flagger);

		// Delete the comment if the size of the flaglist has
		// reached the limit.
		if (c.getFlagList().size() >= FLAGLIMIT)
			deleteComment(c.getId());
		else
			ofy.put(c);
		return true;
	}
}