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
	
	public EditPostDialog(final Post oldPost, final String currentUser) {

		setText("Update post");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		updateButton.getElement().setId("sendButton");
		addItemToDialogPanel(updateButton, 15, 440);
		addItemToDialogPanel(closeButton, 510, 440);
				
		fixBoxes();
		fixLayout();

		getOldValues(oldPost);
		
		if (oldType.equals("video"))
			setTypeBoxSelected(0);
		else if (oldType.equals("picture"))
			setTypeBoxSelected(1);
		else if (oldType.equals("news"))
			setTypeBoxSelected(2);
		else if (oldType.equals("thought"))
			setTypeBoxSelected(3);
		
		setTitleText(oldTitle);
		setPictureText(oldPicture);
		setDescriptionText(oldDescription);
		setTextBoxText(oldText);
		
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
					p.setAuthor(oldAuthor);
					fixUpdateSection(p, currentUser);
					p.setDate(oldDate);
					async.editPost(oldId, p,
							new AsyncCallback<Long>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("PostsPanel.editPost failed \n"
											+ caught);
								}
								@Override
								public void onSuccess(Long result) {
									hide();
								}
							});
				} else {
					Window.alert("Too long text (Max 600 characters) or \n" +
							"description (Max 100 characters). \n " +
							"Otherwise you forgot to fill in all fields \n" +
							"(only Thumbnail can be left empty)");
				}
			}
		});
	}
	
	private void fixUpdateSection(Post p, String currentUser) {
		if (getUpdateBoxValue().equals("Max 300 characters") || 
				getUpdateBoxValue().equals("")) {
			p.setUpdate(getUpdateBoxValue() + "\n Updated by "
					+ currentUser + " at: "
					+ new Date().toString());
		} else {
			p.setUpdate("Updated by "
					+ currentUser + " at: "
					+ new Date().toString());
		}
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