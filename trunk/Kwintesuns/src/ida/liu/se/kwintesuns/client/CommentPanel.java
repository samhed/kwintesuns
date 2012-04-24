package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CommentPanel extends ScrollPanel {
	
	private Long postId;
	private boolean userIsAdmin;
	private FlexTable commentsTable = new FlexTable();
	private FlexTable commentContents = new FlexTable();
	private WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
	public CommentPanel() {
		
		newCommentTextArea.setWidth("95%");
		newCommentTextArea.setWatermark("Write a new comment (Max 300 characters)");
		newCommentTextArea.setCharLimit(300); //for firefox
		newCommentTextArea.getElement().setAttribute("maxlength", "300"); //for chrome
		newCommentTextArea.addKeyDownHandler(new KeyDownHandler() {
		    @Override
		    public void onKeyDown(KeyDownEvent event) {
		        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		        	if (newCommentTextArea.isWithinLimit()) {
		        		String s = newCommentTextArea.getText();
				    	newCommentTextArea.setText("");
		        		async.storeComment(s, postId, new AsyncCallback<Void>() {
						    @Override
						    public void onFailure(Throwable caught) {
						        Window.alert(
						        		"newPost().storePost failed \n" + caught);
						    }
						    @Override
						    public void onSuccess(Void result) {
						    	newCommentTextArea.setText("");
						    	showComments(postId);
						    }
						});
		        	} else {
		        		Window.alert("Too long comment (Max 300 characters).");
		        	}
		        }
		    }
		});
		
		commentsTable.setSize("100%", "100%");
		
		commentContents.setSize("100%", "100%");
		commentContents.getCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_CENTER, 
				HasVerticalAlignment.ALIGN_TOP);
		commentContents.setWidget(0, 0, newCommentTextArea);
		commentContents.getCellFormatter().setAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		commentContents.setWidget(1, 0, commentsTable);
		commentContents.setStyleName("commentPanel");
		
		add(commentContents);
		setSize("100%", "100%");
	}
	
	public void setUserIsAdmin(boolean userIsAdmin) {
		this.userIsAdmin = userIsAdmin;
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
	}
	
	/**
	 * Creates a new comment item to be displayed in the commentsTable
	 * @param comment the current comment from which we get the info
	 * @return the new comment item
	 */
	private FlexTable newCommentItem(Comment comment) {
		
		FlexTable commentItem = new FlexTable();
		commentItem.setStyleName("commentItem");
		commentItem.setWidth("100%");
		commentItem.getColumnFormatter().setWidth(0, "80%");
		commentItem.getColumnFormatter().setWidth(1, "20%");

		Label authorLabel = new Label("by: " + comment.getAuthor());
		Label dateLabel = new Label(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
				.format(comment.getDate()));
		authorLabel.setStyleName("postSmall");
		dateLabel.setStyleName("postSmall");

		commentItem.setText(0, 0, comment.getText());
		
		commentItem.getCellFormatter().setAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		commentItem.getCellFormatter().setAlignment(1, 1,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);

		commentItem.setWidget(0, 1, authorLabel);
		commentItem.setWidget(1, 1, dateLabel);
		
		if (userIsAdmin) {
    		Button removeButton = makeRemoveButton(comment.getId());
        	commentItem.getCellFormatter().setAlignment(0, 2, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_TOP);
        	
        	commentItem.setWidget(0, 2, removeButton);
		}
		
		return commentItem;
	}
	
	/**
	 * Creates a button used to remove a post (for moderators)
	 * @param postId needed for the call to deletePost
	 * @return the remove button
	 */
	private Button makeRemoveButton(final Long commentId) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("removeButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				async.deleteComment(commentId, 
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("CommentPanel.deleteComment failed \n"
										+ caught);
							}
							@Override
							public void onSuccess(Void result) {
								showComments(postId);
							}
						});
			}
		});
		return b;
	}
}
