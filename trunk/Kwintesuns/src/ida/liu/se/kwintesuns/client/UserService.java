package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userservice")
public interface UserService extends RemoteService {	
	ida.liu.se.kwintesuns.client.User getCurrentUser();
	
	Boolean isUserLoggedIn();
}
