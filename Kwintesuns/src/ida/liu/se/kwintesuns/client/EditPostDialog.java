package ida.liu.se.kwintesuns.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

public class EditPostDialog extends NewPostDialog {
	
	private final Button closeButton = new Button("Close");
	private final Button updateButton = new Button("Update");
	
	private String oldAuthor;
	private String oldTitle;
	private String oldType;
	private String oldDescription;
	private String oldPicture;
	private String oldText;
	private String oldUpdate;
	private Date oldDate;
	private Long oldId;
	
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
	public EditPostDialog(final Post oldPost) {

		setText("Update post");
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		updateButton.getElement().setId("sendButton");
		addItemToDialogPanel(updateButton, 15, 440);
		addItemToDialogPanel(closeButton, 510, 440);
		
		getOldValues(oldPost);
		
		if (oldType == "video")
			setTypeBoxSelected(0);
		else if (oldType == "picture")
			setTypeBoxSelected(1);
		else if (oldType == "news")
			setTypeBoxSelected(2);
		else if (oldType == "thought")
			setTypeBoxSelected(3);
		
		setTitleText(oldTitle);
		setPictureText(oldPicture);
		setDescriptionText(oldDescription);
		setTextBoxText(oldText);
		
		fixBoxes();
		fixLayout();
		
		addUpdateSection();
		setUpdateText(oldUpdate);
		
		// Add a handler to close the NewPostDialog
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		updateButton.setFocus(true);
		// If the info is OK store the updated post in the DB
		updateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (newPostFormIsOK()) {
					Post p = getTextBoxValues();
					p.setPoster(oldAuthor);
					p.setUpdate(getUpdateBoxValue() + "\n Updated by "
							+ " at: "
							+ new Date().toString());
					p.setDate(oldDate);
					async.editPost(oldId, p,
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("PostsPanel.editPost failed \n"
											+ caught);
								}
								@Override
								public void onSuccess(Void result) {
									hide();
								}
							});
				} else {
					Window.alert("Too long text (Max 600 characters) or \n" +
							"description (Max 140 characters).");
				}
			}
		});
	}
	
	private void getOldValues(Post post) {
		oldAuthor = post.getAuthor();
		oldTitle = post.getTitle();
		oldType = post.getType();
		oldDescription = post.getDescription();
		oldPicture = post.getPicture();
		oldText = post.getText();
		oldUpdate = post.getUpdate();
		oldDate = post.getDate();
		oldId = post.getId();
	}
}