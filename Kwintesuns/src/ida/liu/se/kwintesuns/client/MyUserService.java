package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("myuserservice")
public interface MyUserService extends RemoteService {	
	MyUser getCurrentMyUser();
	
	void storePost(Post post);
	ArrayList<Post> getAllPosts(); 
	ArrayList<Post> fetchPosts(String filterBy, ArrayList<String> filter) throws IllegalArgumentException;
}
