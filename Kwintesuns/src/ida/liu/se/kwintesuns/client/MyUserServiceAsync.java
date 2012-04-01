package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyUserServiceAsync {
	void getCurrentMyUser(AsyncCallback<MyUser> asyncCallback);

	void storePost(Post post, AsyncCallback<Void> asyncCallback);
	void getAllPosts(AsyncCallback<ArrayList<Post>> asyncCallback); 
	void fetchPosts(String filterBy, ArrayList<String> filter, AsyncCallback<ArrayList<Post>> asyncCallback);
}
