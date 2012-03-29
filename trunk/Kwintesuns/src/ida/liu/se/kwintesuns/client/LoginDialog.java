package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog extends DialogBox {
	
	private final Button closeButton = new Button("Close");
	private VerticalPanel dialogVPanel = new VerticalPanel();
	private final Label textToServerLabel = new Label();
	private final HTML serverResponseLabel = new HTML();
	
	public LoginDialog() {

		setText("Login using OpenID");
		setAnimationEnabled(true);
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>OpenID username:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>OpenID password:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		setWidget(dialogVPanel);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	public Label getTextToServerLabel() {
		return textToServerLabel;
	}

	public HTML getServerResponseLabel() {
		return serverResponseLabel;
	}

	public Button getCloseButton() {
		return closeButton;
	}
}