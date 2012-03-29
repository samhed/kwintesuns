package ida.liu.se.kwintesuns.shared;

import java.io.*;
import java.util.*;

// transports http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/users/User.html info to client
@SuppressWarnings("serial")
public class UserInfo implements Serializable {
        public Boolean isSignedIn;
        public String userId;
        public String email;

        public HashMap<String, String> signInURLs = new HashMap<String, String>();
        public String signOutURL;
}