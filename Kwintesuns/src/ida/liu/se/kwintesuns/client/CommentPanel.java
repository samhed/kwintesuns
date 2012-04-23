package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {
	
	private Long postId;
	private FlexTable commentsTable = new FlexTable();
	private WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
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
		        		Comment c = new Comment(newCommentTextArea.getText(),
		        				postId);
		        		async.storeComment(c, new AsyncCallback<Void>() {
						    @Override
						    public void onFailure(Throwable caught) {
						        Window.alert(
						        		"newPost().storePost failed \n" + caught);
						    }
						    @Override
						    public void onSuccess(Void result) {
						    	showComments(postId);
						    	newCommentTextArea.setText("");
						    }
						});
		        	} else {
		        		Window.alert("Too long comment (Max 300 characters).");
		        	}
		        }
		    }
		});
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		add(commentsTable);
		add(newCommentTextArea);
		
		setHeight("100%");
		setWidth("100%");
	}
	
	public void showComments(Long postId) {
		
		this.postId = postId;
		
		async.getComments(postId, new AsyncCallback<ArrayList<Comment>>() {
			@Override
			public void onFailure(Throwable caught) {
        		Window.alert("showComment.getComments failed \n" + caught);
			}
			@Override
			public void onSuccess(ArrayList<Comment> result) {
	        	updateCommentList(result);
			}
		});
	}
	
	private void updateCommentList(ArrayList<Comment> commentList) {
		
        commentsTable.removeAllRows();
		if (!commentList.isEmpty()) {
	        //loop the array list and comment getters to add 
	        //records to the table
			int row = 0;
	        for (Comment comment : commentList) {
	        	row = commentsTable.getRowCount();
	        	commentsTable.setWidget(row, 0,
	        			newCommentItem(comment));
	        }
		}
		commentsTable.setText(0, 0, "crap");
	}
	
	private FlexTable newCommentItem(Comment comment) {
		
		FlexTable commentItem = new FlexTable();
		commentItem.setStyleName("commentItem");
		commentItem.setWidth("100%");

		commentItem.setText(0, 0, comment.getText());
		commentItem.setText(0, 1, comment.getAuthor());
		commentItem.setText(1, 1, comment.getDate().toString());
		
		return commentItem;
	}
}
