package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("serverservice")
public interface ServerService extends RemoteService {	
	MyUser getCurrentMyUser();
	void subscribe(String emailToSubscribeTo);
	void unsubscribe(String emailToUnsubscribeFrom);
	
	void storePost(Post post);
	void deletePost(Long postId);
	void editPost(Long postId, Post updatedPost);
	ArrayList<Post> getAllPosts();
	ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) throws IllegalArgumentException;
	
	void storeComment(Comment comment);
	void deleteComment(Long commentId);
	ArrayList<Comment> getComments(Long postId);
}
