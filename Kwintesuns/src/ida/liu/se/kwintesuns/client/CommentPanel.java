package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {

	public CommentPanel() {
		final TextBox t3 = new TextBox();
		t3.setText("Comments");
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHeight("100%");
		setWidth("100%");
		
		add(t3);
	}
}
