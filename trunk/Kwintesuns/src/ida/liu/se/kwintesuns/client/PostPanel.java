package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.ui.ScrollPanel;

public class PostPanel extends ScrollPanel{
	
	public PostPanel() {
		setHeight("100%");
		setWidth("100%");
	}
	
	public void addPost(String t, String d, 
			String p, String txt) {
		Post post = new Post(t, d, p, txt);
		//TODO: add post to database
	}
	
	public void refresh() {
		//TODO: check database for posts
		// put posts in the panel
	}

}
