package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerServiceAsync {
	void getCurrentMyUser(AsyncCallback<MyUser> asyncCallback);
	void subscribe(String emailToSubscribeTo, AsyncCallback<Void> asyncCallback);
	void unsubscribe(String emailToUnsubscribeFrom, AsyncCallback<Void> asyncCallback);

	void storePost(Post post, AsyncCallback<Void> asyncCallback);
	void deletePost(Long postId, AsyncCallback<Void> asyncCallback); 
	void editPost(Long postId, Post updatedPost, AsyncCallback<Long> asyncCallback);
	void getAllPosts(AsyncCallback<ArrayList<Post>> asyncCallback); 
	void fetchPosts(String filterBy, ArrayList<String> filter, AsyncCallback<ArrayList<Post>> asyncCallback);
	
	void storeComment(String text, Long postId, AsyncCallback<Void> asyncCallback);
	void deleteComment(Long commentId, AsyncCallback<Void> asyncCallback);
	void getComments(Long postId, AsyncCallback<ArrayList<Comment>> asyncCallback);
}
