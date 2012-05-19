package ida.liu.se.kwintesuns.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

/**
 * The EditPostDialog extends the NewPostDialog and does 
 * therefore have the same layout.
 */
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
	
	public EditPostDialog(final Post oldPost, final String currentUser, final boolean isAdmin) {

		setText("Update post");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		updateButton.getElement().setId("sendButton");
		addItemToDialogPanel(updateButton, 15, 440);
		addItemToDialogPanel(closeButton, 510, 440);
		
		fixBoxes(isAdmin);
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
					async.editPost(oldId, p, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("PostsPanel.editPost failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
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
	
	/**
	 * Checks if the user has written something in the update section
	 * when editing the post, if so add a "updated by... " at the end
	 * otherwise just set the text to "updated by... ".
	 * @param p the post being updated.
	 * @param currentUser the user updating the post.
	 */
	private void fixUpdateSection(Post p, String currentUser) {
		if (!(getUpdateBoxValue().equals("Max 300 characters") || getUpdateBoxValue().equals(""))) {
			// If its a video or picture post being updated, change the urls.
			if (oldType.equals("video") || oldType.equals("picture"))
				p.setText(getUpdateBoxValue());
			else
				p.addToText("\n\n Edit: " + getUpdateBoxValue());
		}
		p.setUpdate("Updated by " + currentUser + " at: " 
				+ DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
	
	/**
	 * Help function called to store the post's old values for use in
	 * the class.
	 * @param post containing all the old values. 
	 */
	private void getOldValues(Post post) {
		oldAuthor = post.getAuthor();
		oldTitle = post.getTitle();
		oldType = post.getType();
		oldDescription = post.getDescription();
		oldPicture = post.getThumbnail();
		oldText = post.getText();
		oldUpdate = post.getUpdate();
		oldDate = post.getDate();
		oldId = post.getId();
	}
}