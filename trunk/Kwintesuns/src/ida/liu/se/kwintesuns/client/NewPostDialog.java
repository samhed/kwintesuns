package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The NewPostDialog appears when the user clicks the "New Post"
 * button in the menu.
 */
public class NewPostDialog extends DialogBox {
	
	private final Button closeButton = new Button("Close");
	private final Button postButton = new Button("Post");
	private AbsolutePanel dialogPanel = new AbsolutePanel();
	
	private TextBox titleBox = new TextBox();
	private ListBox typeBox = new ListBox();
	private WatermarkedTextArea descriptionBox = new WatermarkedTextArea();
	private TextBox pictureBox = new TextBox();
	private WatermarkedTextArea textBox = new WatermarkedTextArea();
	private WatermarkedTextArea updateBox = new WatermarkedTextArea();
	
	private Label titleLabel = new Label("Title:");
	private Label typeLabel = new Label("Type:");
	private Label descriptionLabel = new Label("Description (Max 100 characters):");
	private Label pictureLabel = new Label("Thumbnail (leave empty for default):");
	private Label textLabel = new Label("Video url:");
	private Label updateLabel = new Label("Update:");
	
	private Long newPostId = null; 
	
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
	public NewPostDialog() {

		setText("New post");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		postButton.getElement().setId("sendButton");
		addItemToDialogPanel(postButton, 15, 440);
		addItemToDialogPanel(closeButton, 510, 440);
		
		fixBoxes(true);
		fixLayout();
		
		// Add a handler for when closing the NewPostDialog
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				newPostId = null;
				hide();
			}
		});
		
		postButton.setFocus(true);
		// If the info is OK store the new post in the DB
		postButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (newPostFormIsOK()) {
					Post p = getTextBoxValues();
					async.storePost(p, new AsyncCallback<Long>() {
					    @Override
					    public void onFailure(Throwable caught) {
							newPostId = null;
					        Window.alert("newPost().storePost failed \n" + caught);
					    }
					    @Override
					    public void onSuccess(Long result) {
					    	newPostId = result;
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
	 * Set up attributes for all the fields 
	 * and boxes in the NewPostDialog.
	 * If the user is an admin, allow editing of
	 * all fields.
	 * @param if the user is an admin or not
	 */
	public void fixBoxes(boolean isAdmin) {
		
		// Set readOnly or disable the fields 
		// and boxes depending on isAdmin
		typeBox.setEnabled(isAdmin);
		titleBox.setReadOnly(!isAdmin);
		pictureBox.setReadOnly(!isAdmin);
		descriptionBox.setReadOnly(!isAdmin);
		textBox.setReadOnly(!isAdmin);
		
		typeBox.clear();
		typeBox.addItem("video");
		typeBox.addItem("picture");
		typeBox.addItem("news");
		typeBox.addItem("thought");
		typeBox.setVisibleItemCount(1);
		
		// The textBox is used for all four types of posts.
		// * Video: used to pass the url of the video
		// * Picture: used to pass the url of the image
		// * News & Thoughts: the main text of the post
		typeBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int i = typeBox.getSelectedIndex();
				if (i == 0)
					textLabel.setText("Video url:");
				else if (i == 1)
					textLabel.setText("Picture url:");
				else if ((i == 2) || (i == 3))
					textLabel.setText("Text (Max 600 characters):");
			}
		});
		
		titleBox.setWidth("240px");
		titleBox.setMaxLength(40);
		pictureBox.setMaxLength(200);
		pictureBox.setWidth("170px");
		descriptionBox.setSize("546px", "67px");
		descriptionBox.setWatermark("Max 100 characters");
		descriptionBox.setCharLimit(100); //for firefox
		descriptionBox.getElement().setAttribute("maxlength", "100"); //for chrome
		textBox.setSize("546px", "220px");
		textBox.setWatermark("Max 600 characters");
		textBox.setCharLimit(600); //for firefox
		textBox.getElement().setAttribute("maxlength", "600"); //for chrome
	}
	
	/**
	 * Places the fields and boxes into the dialogPanel.
	 */
	public void fixLayout() {
		dialogPanel.setSize("600px", "500px");
		dialogPanel.setStyleName("dialogPanel");
		dialogPanel.add(titleLabel, 15, 10);
		dialogPanel.add(titleBox, 17, 25);
		dialogPanel.add(typeLabel, 295, 10);
		dialogPanel.add(typeBox, 295, 25);
		dialogPanel.add(pictureLabel, 390, 10);
		dialogPanel.add(pictureBox, 392, 25);
		dialogPanel.add(descriptionLabel, 15, 70);
		dialogPanel.add(descriptionBox, 17, 87);
		dialogPanel.add(textLabel, 15, 170);
		dialogPanel.add(textBox, 17, 187);
		
		setWidget(dialogPanel);
	}
	
	/**
	 * Modifies the dialogPanel to contain a update field.
	 */
	public void addUpdateSection() {
		textBox.setSize("546px", "120px");
		
		updateBox.setSize("546px", "70px");
		updateBox.setWatermark("Max 300 characters");
		updateBox.setCharLimit(300); //for firefox
		updateBox.getElement().setAttribute("maxlength", "300"); //for chrome
		
		dialogPanel.add(updateLabel, 15, 325);
		dialogPanel.add(updateBox, 17, 342);
	}
	
	/**
	 * A help function for adding items to the dialogPanel.
	 * @param w the widget to add
	 * @param left offset from the left side of the panel
	 * @param top offset from the top of the panel
	 */
	public void addItemToDialogPanel(Widget w, int left, int top) {
		dialogPanel.add(w, left, top);
	}
	
	/**
	 * Check if the info specified by the user is OK
	 * @return whether it is OK or not
	 */
	public boolean newPostFormIsOK() {
		return descriptionBox.isWithinLimit() 
				&& textBox.isWithinLimit()
				&& !titleBox.getText().isEmpty() 
				&& !descriptionBox.isEmpty()
				&& !textBox.isEmpty();
	}
	
	// Setters and getters for the fields and boxes:
	
	public void setTypeBoxSelected(int i) {typeBox.setSelectedIndex(i);}	
	public void setTitleText(String s) {titleBox.setText(s);}	
	public void setDescriptionText(String s) {descriptionBox.setText(s);}	
	public void setPictureText(String s) {pictureBox.setText(s);}
	public void setTextBoxText(String s) {textBox.setText(s);}	
	public void setUpdateText(String s) {updateBox.setText(s);}

	public Post getTextBoxValues() {
		return new Post(
			titleBox.getText(),
			typeBox.getItemText(typeBox.getSelectedIndex()),
			descriptionBox.getText(),
			pictureBox.getText(),
			textBox.getText());
	}
	
	public String getUpdateBoxValue() {return updateBox.getText();}
	public Long getNewPostId() {return newPostId;}
}