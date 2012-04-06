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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class NewPostDialog extends DialogBox {
	
	private final Button closeButton = new Button("Close");
	private final Button sendButton = new Button("Post");
	private AbsolutePanel dialogPanel = new AbsolutePanel();
	private TextBox titleBox = new TextBox();
	private ListBox typeBox = new ListBox();
	private TextArea descriptionBox = new TextArea();
	private TextBox pictureBox = new TextBox();
	private TextArea textBox = new TextArea();
	private Label titleLabel = new Label("Title:");
	private Label typeLabel = new Label("Type:");
	private Label descriptionLabel = new Label("Description:");
	private Label pictureLabel = new Label("Picture (leave empty for default):");
	private Label textLabel = new Label("Text:");
	private final MyUserServiceAsync async = GWT.create(MyUserService.class);
	
	public NewPostDialog() {

		setText("New post");
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		sendButton.getElement().setId("sendButton");
		descriptionBox.setSize("546px", "67px");
		textBox.setSize("546px", "220px");
		
		typeBox.addItem("video");
		typeBox.addItem("picture");
		typeBox.addItem("news");
		typeBox.addItem("thought");
		typeBox.setVisibleItemCount(1);
		
		dialogPanel.setSize("600px", "500px");
		dialogPanel.setStyleName("dialogPanel");
		dialogPanel.add(titleLabel, 15, 10);
		dialogPanel.add(titleBox, 15, 25);
		dialogPanel.add(typeLabel, 210, 10);
		dialogPanel.add(typeBox, 210, 25);
		dialogPanel.add(pictureLabel, 410, 10);
		dialogPanel.add(pictureBox, 410, 25);
		dialogPanel.add(descriptionLabel, 15, 70);
		dialogPanel.add(descriptionBox, 17, 87);
		dialogPanel.add(textLabel, 15, 170);
		dialogPanel.add(textBox, 17, 187);
		dialogPanel.add(sendButton, 15, 440);
		dialogPanel.add(closeButton, 510, 440);
		
		setWidget(dialogPanel);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		sendButton.setFocus(true);
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Post p = new Post(
					titleBox.getText(),
					typeBox.getItemText(typeBox.getSelectedIndex()),
					descriptionBox.getText(),
					pictureBox.getText(),
					textBox.getText());
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
			}
		});
	}
}