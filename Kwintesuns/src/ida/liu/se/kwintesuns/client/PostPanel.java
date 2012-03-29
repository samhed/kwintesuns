package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PostPanel extends VerticalPanel{
	
	public PostPanel() {

		final TextBox t2 = new TextBox();
		t2.setText("Post");

		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHeight("100%");
		setWidth("100%");
		add(t2);
	}

}
