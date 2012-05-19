package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TopPanel extends VerticalPanel {
	
	private ContentPanel contentPanel;
	private Label header = new Label();
	private Label loggedInLabel = new Label();
	private MenuBar postMenu = new MenuBar();
	private MenuBar filterMenu = new MenuBar();
	private MenuBar loginMenu = new MenuBar();
	private MenuItem loginButton;
	private MenuItem subscriptionsButton;
	private String loginButtonText = "Login";
	private MyUser user;
	private FlexTable menuGrid = new FlexTable();
	private FlexTable headerGrid = new FlexTable();
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	
	// ClickHandler for the header and refreshButton
	private ClickHandler refreshHandler = new ClickHandler() {
	    @Override
	    public void onClick (ClickEvent event){
	    	init();
	    	contentPanel.init();
	    }
	};
	
	public TopPanel(final ContentPanel contentPanel) {
		
		this.contentPanel = contentPanel;
		
		// Header:
		header.setText("KWINTESUNS");
		header.setStyleName("headerStyle");
		header.addClickHandler(refreshHandler);
		
		// Label showing login status:
		loggedInLabel.setStyleName("topBannerText");
		if (loggedInLabel.getText().equals(""))
	        loggedInLabel.setText("Not logged in");	
		
		// The headerGrid contains the header and loggedInLabel
		headerGrid.setSize("100%", "55px");
		headerGrid.getColumnFormatter().setWidth(1, "100%");
		headerGrid.getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_TOP);
		headerGrid.setWidget(0, 0, header);
		headerGrid.setWidget(0, 1, loggedInLabel);
		
		// Button for refreshing the website
		Button refreshButton = new Button();
		refreshButton.setSize("37px", "33px");
		refreshButton.setStyleName("refreshMenu");
		refreshButton.addClickHandler(refreshHandler);
		
		// The subscriptionsButton and loginButton, needs some 
		// configuring compared to the rest of the menu buttons.
		subscriptionsButton = new MenuItem("Subscriptions", showSubscribe);
        subscriptionsButton.setVisible(false);
        loginButton = new MenuItem(loginButtonText, login);
		if (loginButtonText.equals("Logout"))
			loginButtonText = "Login";
		
		// Setting up the menus:
    	postMenu.addStyleName("MenuBar");
    	postMenu.addItem("New Post", newPost);    	
		filterMenu.addStyleName("MenuBar");
		filterMenu.addItem("Videos", showVideos);
		filterMenu.addItem("Pictures", showPictures);
		filterMenu.addItem("News", showNews);
		filterMenu.addItem("Thoughts", showThoughts);
        filterMenu.addItem(subscriptionsButton);
        loginMenu.addItem(loginButton);
		loginMenu.addStyleName("MenuBar");
		
		// The menuGrid contains the refreshButton, 
		// postMenu, filterMenu and loginMenu
		menuGrid.setSize("100%", "35px");
		menuGrid.getColumnFormatter().setWidth(0, "37px");
		menuGrid.getColumnFormatter().setWidth(1, "100px");
		menuGrid.getColumnFormatter().setWidth(2, "*");
		menuGrid.getColumnFormatter().setWidth(3, "59px");
		menuGrid.getFlexCellFormatter().setAlignment(0, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_MIDDLE);
		menuGrid.setWidget(0, 0, refreshButton);
		menuGrid.setWidget(0, 1, postMenu);
		menuGrid.setWidget(0, 2, filterMenu);
		menuGrid.setWidget(0, 3, loginMenu);
		
		// The topPanel contains the headerGrid and menuGrid
		setStyleName("topPanel");
		add(headerGrid);
		add(menuGrid);
		setSize("100%", "90px");
	}

	/**
	 * Update loggedInLabel, loginButton & friendsButton
	 */
	public void init() {		
		async.getCurrentMyUser(new AsyncCallback<MyUser>() {
		    @Override
		    public void onFailure(Throwable caught) {
		        Window.alert("init().getCurrentMyUser failed\n" + caught);
		        if (loggedInLabel.getText().equals("") || 
		        		loggedInLabel.getText().equals("Not logged in"))
		            loggedInLabel.setText("logging in...");
		        else
			        loggedInLabel.setText("logging out...");
		    }
		    @Override
		    public void onSuccess(MyUser result) {
		        if (result == null) { 
		        	//No user is logged in
		        	loginButtonText = "Login";
		            loginButton.setText(loginButtonText);
		            loggedInLabel.setText("Not logged in");
		            subscriptionsButton.setVisible(false);
		        } else {
		        	loginButtonText = "Logout";
		            loginButton.setText(loginButtonText);
		            user = result;
		            loggedInLabel.setText("Logged in as: " + user.getEmail());
		            subscriptionsButton.setVisible(true);
		        }
		    }
		});
	}
	
	/**
	 * Add the type filters to an ArrayList before sending them 
	 * to showPostList for compatibility reasons.
	 * @param filter a string with the filter to apply
	 */
	private void applyTypeFilter(String filter) {
		ArrayList<String> filterArray = new ArrayList<String>();
		filterArray.add(filter);
		contentPanel.showPostList("type", filterArray);
	}

	// Menu commands:
	private final Command newPost = new Command() {@Override public void execute() {contentPanel.newPostDialog();}};	
	private final Command showVideos = new Command() {@Override	public void execute() {applyTypeFilter("video");}};
	private final Command showPictures = new Command() {@Override public void execute() {applyTypeFilter("picture");}};
	private final Command showNews = new Command() {@Override public void execute() {applyTypeFilter("news");}};
	private final Command showThoughts = new Command() {@Override public void execute() {applyTypeFilter("thought");}};
	// The showSubscribe command does not need to use the applyTypeFilter function 
	// since the subscription list is already an ArrayList. 
	private final Command showSubscribe = new Command() {
		@Override 
		public void execute() {
			async.getCurrentMyUser(new AsyncCallback<MyUser>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("showSubscribe.getCurrentMyUser failed \n" + caught);
				}
				@Override
				public void onSuccess(MyUser result) {
					contentPanel.showPostList("author", result.getSubscriptionList());
				}
			});
		}
	};
	// Redirect the users' browser to OpenID
	private final Command login = new Command() {
		@Override
		public void execute() {
			Window.Location.replace("/_ah/OpenID");
		}
	};
}