package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {
	
	private WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
	
	public CommentPanel() {
		
		newCommentTextArea.setWidth("98.5%");
		newCommentTextArea.setWatermark("Write a new comment (Max 300 characters)");
		newCommentTextArea.setCharLimit(300); //for firefox
		newCommentTextArea.getElement().setAttribute("maxlength", "300"); //for chrome
		newCommentTextArea.addKeyDownHandler(new KeyDownHandler() {
		    @Override
		    public void onKeyDown(KeyDownEvent event) {
		        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		        	if (newCommentTextArea.isWithinLimit()) {
			        	//post comment
		        	} else {
		        		Window.alert("Too long comment (Max 300 characters).");
		        	}
		        }
		    }
		});
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		add(newCommentTextArea);
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHeight("100%");
		setWidth("100%");
	}
}
