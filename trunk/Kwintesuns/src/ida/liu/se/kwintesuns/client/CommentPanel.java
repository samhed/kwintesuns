package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {
	
	private WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
	
	public CommentPanel() {
		
		newCommentTextArea.setWidth("98.5%");
		newCommentTextArea.setWatermark("Write a new comment");
		newCommentTextArea.addKeyDownHandler(new KeyDownHandler() {
		    @Override
		    public void onKeyDown(KeyDownEvent event) {
		        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		        	//post comment
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
