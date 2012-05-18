package ida.liu.se.kwintesuns.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class OpenIDServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser(); // or req.getUserPrincipal()

        resp.setContentType("text/html");

        // Depending if the user is logged in or not 
        // a login or logout will be created with the
        // return address attached(top level).
        if (user == null)
        	resp.sendRedirect(userService.createLoginURL("/"));
        else
        	resp.sendRedirect(userService.createLogoutURL("/"));
    }
}