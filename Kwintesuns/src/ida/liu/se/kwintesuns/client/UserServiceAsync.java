package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {
	void getCurrentUser(AsyncCallback<ida.liu.se.kwintesuns.client.User> asyncCallback);
	
	void isUserLoggedIn(AsyncCallback<Boolean> callback);
}
