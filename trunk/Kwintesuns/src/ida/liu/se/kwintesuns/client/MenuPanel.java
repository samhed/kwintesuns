package ida.liu.se.kwintesuns.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuPanel extends VerticalPanel {
	
	private Label header = new Label();
	
	public MenuPanel() {
		header.setText("Kwintesuns");
		header.setStyleName("headerStyle");
		
		MenuBar menu = new MenuBar();
		menu.addStyleName("MenuBar");
		menu.addItem("Videos", showVideos);
		menu.addItem("Pictures", showPictures);
		menu.addItem("News", showNews);
		menu.addItem("Thoughts", showThoughts);
		menu.addItem("Login", login);
		
		setStyleName("topPanel");
		add(header);
		add(menu);
		setWidth("100%");
	}
	
	private final Command showVideos = new Command() {
		@Override
		public void execute() {
			
		}
	};
	private final Command showPictures = new Command() {
		@Override
		public void execute() {
			
		}
	};
	private final Command showNews = new Command() {
		@Override
		public void execute() {
			
		}
	};
	private final Command showThoughts = new Command() {
		@Override
		public void execute() {
			
		}
	};
	private final Command login = new Command() {
		@Override
		public void execute() {
			LoginDialog loginDialog = new LoginDialog();
			loginDialog.getServerResponseLabel().setHTML("hej");
			loginDialog.center();
			loginDialog.getCloseButton().setFocus(true);
		}
	};
}

/*
	private FlexTable menuTable = new FlexTable();
	
	private Button videosButton = new Button();
	private Button picturesButton = new Button();
	private Button newsButton = new Button();
	private Button thoughtsButton = new Button();
	
videosButton.setStyleName("menuButton");
videosButton.setText("Videos");
videosButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	}
});	
picturesButton.setStyleName("menuButton");
picturesButton.setText("Pictures");
picturesButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	}
});	
newsButton.setStyleName("menuButton");
newsButton.setText("News");
newsButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	}
});	
thoughtsButton.setStyleName("menuButton");
thoughtsButton.setText("Thoughts");
thoughtsButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	}
});	

menuTable.setWidget(0, 0, videosButton);
menuTable.setWidget(0, 1, picturesButton);
menuTable.setWidget(0, 2, newsButton);
menuTable.setWidget(0, 3, thoughtsButton);*/