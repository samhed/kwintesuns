package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("serverservice")
public interface ServerService extends RemoteService {	
	MyUser getCurrentMyUser();
	void subscribe(String emailToSubscribeTo);
	void unsubscribe(String emailToUnsubscribeFrom);
	
	Long storePost(Post post);
	void deletePost(Long postId);
	Long editPost(Long postId, Post updatedPost);
	void flagPost(Long postId, String flagger);
	ArrayList<Post> getAllPosts();
	ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter);
	
	Long storeComment(String text, Long postId);
	void deleteComment(Long commentId);
	ArrayList<Comment> getComments(Long postId);
	void flagComment(Long commentId, String flagger);
}
