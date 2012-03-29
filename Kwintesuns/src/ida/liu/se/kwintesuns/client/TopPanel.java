package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TopPanel extends VerticalPanel {
	
	private Label header = new Label();
	private MenuBar mainMenu = new MenuBar();
	private MenuBar loginMenu = new MenuBar();
	private MenuItem loginButton;
	private Label loggedInLabel = new Label();
	private FlexTable menuGrid = new FlexTable();
	private final UserServiceAsync async = GWT.create(UserService.class);
	
	public TopPanel() {
		header.setText("KWINTESUNS");
		header.setStyleName("headerStyle");
		
		mainMenu.addStyleName("MenuBar");
		mainMenu.addItem("Videos", showVideos);
		mainMenu.addItem("Pictures", showPictures);
		mainMenu.addItem("News", showNews);
		mainMenu.addItem("Thoughts", showThoughts);

		loginMenu.addStyleName("MenuBar");
		loginButton = loginMenu.addItem("Login", login);
		
        loggedInLabel.setText("Not logged in");
		
		menuGrid.setWidth("100%");;
		menuGrid.getColumnFormatter().setWidth(0, "100%");
		menuGrid.getColumnFormatter().setWidth(1, "auto");
		menuGrid.setWidget(0, 0, mainMenu);
		menuGrid.getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_MIDDLE);
		menuGrid.setWidget(0, 1, loginMenu);
		
		setStyleName("topPanel");
		add(header);
		add(loggedInLabel);
		add(menuGrid);
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
			Window.Location.replace("/_ah/OpenID");
			/*
			LoginDialog loginDialog = new LoginDialog();
			loginDialog.getServerResponseLabel().setHTML("hej");
			loginDialog.center();
			loginDialog.getCloseButton().setFocus(true);
			*/
			
			async.getCurrentUser(new AsyncCallback<User>() {

	            @Override
	            public void onFailure(Throwable caught) {
	                Window.alert("LoginWidget RPC throwable\n" + caught);
	            }

	            @Override
	            public void onSuccess(User result) {
	                if (result == null) { //No user is logged in
	                    loginButton.setText("Login");
	                    loggedInLabel.setText("Not logged in");
	                } else {
	                    loginButton.setText("Logout");
	                    loggedInLabel.setText("logged in as: " + result.getName());
	                }
	            }
	        });
		}
	};
}