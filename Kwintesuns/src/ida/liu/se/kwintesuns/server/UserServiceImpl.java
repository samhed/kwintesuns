package ida.liu.se.kwintesuns.server;
import java.util.Set;

import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements
com.google.appengine.api.users.UserService {

	@Override
	public User getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLoginURL(String destinationURL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLoginURL(String destinationURL, String authDomain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLoginURL(String destinationURL, String authDomain,
			String federatedIdentity, Set<String> attributesRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLogoutURL(String destinationURL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLogoutURL(String destinationURL, String authDomain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserAdmin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}
}
