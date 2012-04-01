package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class PostPanel extends ScrollPanel{
	
	private FlexTable postsTable;
	
	public PostPanel(FlexTable pT) {		
		this.postsTable = pT;
		
		setHeight("100%");
		setWidth("100%");
	}
	
	public void refresh() {
		remove(postsTable);
		add(postsTable);
		//TODO: check database for posts
		// put posts in the panel
	}

}
