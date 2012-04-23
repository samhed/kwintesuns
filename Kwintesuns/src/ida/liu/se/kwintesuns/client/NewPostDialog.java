package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.GWT;
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
	private final Button sendButton = new Button("Post");
	private AbsolutePanel dialogPanel = new AbsolutePanel();
	
	private TextBox titleBox = new TextBox();
	private ListBox typeBox = new ListBox();
	private WatermarkedTextArea descriptionBox = new WatermarkedTextArea();
	private TextBox pictureBox = new TextBox();
	private WatermarkedTextArea textBox = new WatermarkedTextArea();
	private WatermarkedTextArea updateBox = new WatermarkedTextArea();
	
	private Label titleLabel = new Label("Title:");
	private Label typeLabel = new Label("Type:");
	private Label descriptionLabel = new Label("Description (Max 140 characters):");
	private Label pictureLabel = new Label("Picture (leave empty for default):");
	private Label textLabel = new Label("Text (Max 600 characters):");
	private Label updateLabel = new Label("Update:");
	
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
	public NewPostDialog() {

		setText("New post");
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		sendButton.getElement().setId("sendButton");
		addItemToDialogPanel(sendButton, 15, 440);
		addItemToDialogPanel(closeButton, 510, 440);
		
		fixBoxes();
		fixLayout();
		
		// Add a handler to close the NewPostDialog
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		sendButton.setFocus(true);
		// If the info is OK store the new post in the DB
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (newPostFormIsOK()) {
					Post p = getTextBoxValues();
					async.storePost(p, new AsyncCallback<Void>() {
					    @Override
					    public void onFailure(Throwable caught) {
					        Window.alert(
					        		"newPost().storePost failed \n" + caught);
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
	
	public void fixBoxes() {		
		typeBox.addItem("video");
		typeBox.addItem("picture");
		typeBox.addItem("news");
		typeBox.addItem("thought");
		typeBox.setVisibleItemCount(1);
		
		titleBox.setWidth("240px");
		titleBox.setMaxLength(40);
		pictureBox.setMaxLength(200);
		pictureBox.setWidth("170px");
		descriptionBox.setSize("546px", "67px");
		descriptionBox.setWatermark("Max 140 characters");
		descriptionBox.setCharLimit(140); //for firefox
		descriptionBox.getElement().setAttribute("maxlength", "140"); //for chrome
		textBox.setSize("546px", "220px");
		textBox.setWatermark("Max 600 characters");
		textBox.setCharLimit(600); //for firefox
		textBox.getElement().setAttribute("maxlength", "600"); //for chrome
	}
	
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
	
	public Post getTextBoxValues() {
		return new Post(
			titleBox.getText(),
			typeBox.getItemText(typeBox.getSelectedIndex()),
			descriptionBox.getText(),
			pictureBox.getText(),
			textBox.getText());
	}
	
	public String getUpdateBoxValue() {
		return updateBox.getText();
	}
	
	public void addUpdateSection() {
		textBox.setSize("546px", "120px");
		
		updateBox.setSize("546px", "70px");
		updateBox.setWatermark("Max 300 characters");
		updateBox.setCharLimit(300); //for firefox
		updateBox.getElement().setAttribute("maxlength", "300"); //for chrome
		
		dialogPanel.add(updateLabel, 15, 325);
		dialogPanel.add(updateBox, 17, 342);
	}
	
	public void addItemToDialogPanel(Widget w, int left, int top) {
		dialogPanel.add(w, left, top);
	}
	
	// Check if the info specified by the user is OK
	public boolean newPostFormIsOK() {
		return descriptionBox.isWithinLimit() && textBox.isWithinLimit();
	}
	
	public void setTypeBoxSelected(int i) {
		typeBox.setSelectedIndex(i);
	}
	
	public void setTitleText(String s) {
		titleBox.setText(s);
	}
	
	public void setDescriptionText(String s) {
		descriptionBox.setText(s);
	}
	
	public void setPictureText(String s) {
		pictureBox.setText(s);
	}
	
	public void setTextBoxText(String s) {
		textBox.setText(s);
	}
	
	public void setUpdateText(String s) {
		updateBox.setText(s);
	}
}