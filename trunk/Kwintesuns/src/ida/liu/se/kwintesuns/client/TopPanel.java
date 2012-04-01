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
	
	private Label header = new Label();
	private Label loggedInLabel;
	private MenuBar postMenu = new MenuBar();
	private MenuBar filterMenu = new MenuBar();
	private MenuBar loginMenu = new MenuBar();
	private MenuItem loginButton;
	private MenuItem friendsButton;
	private String loginButtonText;
	private MyUser user;
	private ArrayList<String> filter = new ArrayList<String>();
	private FlexTable menuGrid = new FlexTable();
	private FlexTable headerGrid = new FlexTable();
	private FlexTable postsTable;
	private final MyUserServiceAsync async = GWT.create(MyUserService.class);
	
	public TopPanel(Label lIL, String lBT, final String baseUrl, FlexTable pT) {
		
		this.postsTable = pT;
		this.loggedInLabel = lIL;
		this.loginButtonText = lBT;
				
		header.setText("KWINTESUNS");
		header.setStyleName("headerStyle");
		header.addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick (ClickEvent event){
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
	
	public void refresh() {
		async.getCurrentMyUser(new AsyncCallback<MyUser>() {
		    @Override
		    public void onFailure(Throwable caught) {
		        //Window.alert("LoginWidget RPC throwable\n" + caught);
	            loggedInLabel.setText("logging in...");
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
	
	private final Command newPost = new Command() {
		@Override
		public void execute() {
			async.storePost(new Post(
					"hej", "video", "first", "picture", 
					"detta är den första posten"),
					new AsyncCallback<Void>() {
				    @Override
				    public void onFailure(Throwable caught) {
				        Window.alert(
				        		"Store Post failed \n" + caught);
				    }
				    @Override
				    public void onSuccess(Void result) {
				    	//TODO: hide newPost popup
				    }
				});
		}
	};	
	private final Command showVideos = new Command() {
		@Override
		public void execute() {
			filter.clear();
			filter.add("videos");
			makePostList("type");
		}
	};
	private final Command showPictures = new Command() {
		@Override
		public void execute() {
			filter.clear();
			filter.add("pictures");
			makePostList("type");
		}
	};
	private final Command showNews = new Command() {
		@Override
		public void execute() {
			filter.clear();
			filter.add("news");
			makePostList("type");
		}
	};
	private final Command showThoughts = new Command() {
		@Override
		public void execute() {
			filter.clear();
			filter.add("thoughts");
			makePostList("type");
		}
	};
	private final Command showFriends = new Command() {
		@Override
		public void execute() {
			filter = user.getFriendList();
			makePostList("poster");
		}
	};
	private final Command login = new Command() {
		@Override
		public void execute() {
			Window.Location.replace("/_ah/OpenID");
		}
	};
	
	private void makePostList(String filterBy) {
		async.fetchPosts(filterBy, filter,
            new AsyncCallback<ArrayList<Post>>() {
		        @Override
		        public void onFailure(Throwable caught) {
		      	  Window.alert("Fetch Posts failed \n"
		                  + caught);
		        }
		        public void onSuccess(ArrayList<Post> result) {
		        	int row = 0;
		            postsTable.removeAllRows();
		            postsTable.setText(0, 0, "Type");
		            postsTable.setText(0, 1, "Date");
		            postsTable.setText(0, 2, "Title");
		            postsTable.setText(0, 3, "Poster");
		            postsTable.setText(0, 4, "Text");
		            //loop the array list and user getters to add 
		            //records to the table
		            for (Post post : result) {
		              row = postsTable.getRowCount();
		              postsTable.setText(row, 0,
		            		  post.getType());
		              postsTable.setText(row, 1,
		            		  post.getDate().toString());
		              postsTable.setText(row, 2,
		            		  post.getTitle());
		              postsTable.setText(row, 3,
		            		  post.getPoster());
		              postsTable.setText(row, 4,
		            		  post.getText());
		            }
		        }
		      });        
	}
}

/*
LoginDialog loginDialog = new LoginDialog();
loginDialog.getServerResponseLabel().setHTML("hej");
loginDialog.center();
loginDialog.getCloseButton().setFocus(true);
*/