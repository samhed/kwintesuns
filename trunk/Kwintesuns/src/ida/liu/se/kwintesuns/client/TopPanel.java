package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	private PostsPanel postsPanel;
	private Label header = new Label();
	private Label loggedInLabel = new Label();
	private MenuBar postMenu = new MenuBar();
	private MenuBar filterMenu = new MenuBar();
	private MenuBar loginMenu = new MenuBar();
	private String baseUrl;
	private MenuItem loginButton;
	private MenuItem friendsButton;
	private String loginButtonText;
	private MyUser user;
	private FlexTable menuGrid = new FlexTable();
	private FlexTable headerGrid = new FlexTable();
	private final MyUserServiceAsync async = GWT.create(MyUserService.class);
	
	public TopPanel(PostsPanel postsPanel) {
		
		this.postsPanel = postsPanel;
				
		header.setText("KWINTESUNS");
		header.setStyleName("headerStyle");
		header.addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick (ClickEvent event){
		    	if ((baseUrl == "") || (baseUrl == null))
			    	baseUrl = Window.Location.getHref();
				Window.Location.replace(baseUrl);
		    }
		});
		
		loggedInLabel.setStyleName("topBannerText");
		if ((loggedInLabel.getText() == null) || (loggedInLabel.getText() == ""))
	        loggedInLabel.setText("Not logged in");	
		
    	postMenu.addStyleName("MenuBar");
    	postMenu.addItem("New Post", newPost);
        
		filterMenu.addStyleName("MenuBar");
		filterMenu.addItem("Videos", showVideos);
		filterMenu.addItem("Pictures", showPictures);
		filterMenu.addItem("News", showNews);
		filterMenu.addItem("Thoughts", showThoughts);
        friendsButton = filterMenu.addItem("Friends", showFriends);
        friendsButton.setVisible(false);

		loginMenu.addStyleName("MenuBar");
		if ((loginButtonText == "") || (loginButtonText == null))
			loginButtonText = "Login";
		loginButton = loginMenu.addItem(loginButtonText, login);
		
		headerGrid.setWidth("100%");
		headerGrid.getColumnFormatter().setWidth(1, "100%");
		headerGrid.getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_TOP);
		headerGrid.setWidget(0, 0, header);
		headerGrid.setWidget(0, 1, loggedInLabel);
		
		menuGrid.setWidth("100%");
		menuGrid.getColumnFormatter().setWidth(0, "90px");
		menuGrid.getColumnFormatter().setWidth(1, "*");
		menuGrid.getColumnFormatter().setWidth(2, "59px");
		menuGrid.getFlexCellFormatter().setAlignment(0, 2, 
				HasHorizontalAlignment.ALIGN_RIGHT, 
				HasVerticalAlignment.ALIGN_MIDDLE);
		menuGrid.setWidget(0, 0, postMenu);
		menuGrid.setWidget(0, 1, filterMenu);
		menuGrid.setWidget(0, 2, loginMenu);
		
		setStyleName("topPanel");
		add(headerGrid);
		add(menuGrid);
		setHeight("90px");
		setWidth("100%");
	}

	//update loggedInLabel, loginButton & friendsButton
	public void refresh() {		
		async.getCurrentMyUser(new AsyncCallback<MyUser>() {
		    @Override
		    public void onFailure(Throwable caught) {
		        Window.alert("refresh().getCurrentMyUser failed\n" + caught);
		        if ((loggedInLabel.getText() == null) || (loggedInLabel.getText() == ""))
		            loggedInLabel.setText("logging in...");
		        else
			        loggedInLabel.setText("logging out...");
		    }
		    @Override
		    public void onSuccess(MyUser result) {
		        if (result == null) { //No user is logged in
		        	loginButtonText = "Login";
		            loginButton.setText(loginButtonText);
		            loggedInLabel.setText("Not logged in");
		            friendsButton.setVisible(false);
		        } else {
		        	loginButtonText = "Logout";
		            loginButton.setText(loginButtonText);
		            user = result;
		            loggedInLabel.setText("Logged in as: " + user.getFederatedId());
		            friendsButton.setVisible(true);
		        }
		    }
		});
	}
	
	private void applyTypeFilter(String filter) {
		ArrayList<String> filterArray = new ArrayList<String>();
		filterArray.add(filter);
		postsPanel.showPostList("type", filterArray);
	}
	
	private final Command newPost = new Command() {
		@Override
		public void execute() {postsPanel.newPost();}
	};	
	private final Command showVideos = new Command() {
		@Override
		public void execute() {applyTypeFilter("video");}
	};
	private final Command showPictures = new Command() {
		@Override
		public void execute() {applyTypeFilter("picture");}
	};
	private final Command showNews = new Command() {
		@Override
		public void execute() {applyTypeFilter("news");}
	};
	private final Command showThoughts = new Command() {
		@Override
		public void execute() {applyTypeFilter("thought");}
	};	
	private final Command showFriends = new Command() {
		@Override
		public void execute() {postsPanel.showPostList("poster", user.getFriendList());}
	};
	private final Command login = new Command() {
		@Override
		public void execute() {Window.Location.replace("/_ah/OpenID");}
	};
}