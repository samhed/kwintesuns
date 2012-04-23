package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class PostsPanel extends ScrollPanel{
	
	private FlexTable postsTable = new FlexTable();
	private final NewPostDialog newPostDialog = new NewPostDialog();
	private final ServerServiceAsync async = GWT.create(ServerService.class);
	private ArrayList<Post> postList;
	private int selectedPost;
	private String currentUser = "";
	private boolean userIsAdmin = false;
	
	private AsyncCallback<MyUser> checkUserCallback = new AsyncCallback<MyUser>() {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("PostsPanel.checkUserCallback failed \n"
				+ caught);
		}
		@Override
		public void onSuccess(MyUser result) {
			if (result != null) {
				userIsAdmin = result.isAdministrator();
				currentUser = result.getFederatedId();
			}
		}
    };
	
    private CloseHandler<PopupPanel> dialogCloseHandler = new CloseHandler<PopupPanel>() {
		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			//Window.alert("newPostDialog closed");
			initPosts();
		}
	};
    
	private static final Map<String, String> defaultTypeImageUrls;
	static {
		defaultTypeImageUrls = new HashMap<String, String>();
		defaultTypeImageUrls.put("video", "img/yt.jpg");
		defaultTypeImageUrls.put("picture", "img/img.jpg");
		defaultTypeImageUrls.put("news", "img/earth.png");
		defaultTypeImageUrls.put("thought", "img/thought.png");
		defaultTypeImageUrls.put("error", "img/error.png");
	}
	
	public PostsPanel() {
		
		postsTable.setWidth("100%");
		add(postsTable);
		
		setWidth("100%");
	}
	
	public void initPosts() {
		ServerServiceAsync async = GWT.create(ServerService.class);
		async.getAllPosts(new AsyncCallback<ArrayList<Post>>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("initPosts().getAllPosts failed \n"
						+ caught);
				}
				@Override
				public void onSuccess(ArrayList<Post> result) {
					updatePostList(result);
				}
			});
	}
	
	private void updatePostList(ArrayList<Post> result) {
		postList = result;
        postsTable.removeAllRows();
		if (!result.isEmpty()) {
	        //loop the array list and post getters to add 
	        //records to the table
			int row = 0;
	        for (Post post : postList) {
	        	row = postsTable.getRowCount();
	        	postsTable.setWidget(row, 0,
	        			newPostItem(post));
	        }
	        Post first = result.get(0);
			postItemExpand((FlexTable) postsTable.getWidget(0, 0), first);
			selectedPost = 0;
			compressNonSelectedPostItems();
		}
	}
	
	private FlexTable newPostItem(final Post post) {

		final FlexTable postItem = new FlexTable();
		postItem.setStyleName("postItem");
		postItem.setWidth("100%");
		postItem.getColumnFormatter().setWidth(1, "25%");
		postItem.getColumnFormatter().setWidth(2, "75%");
				
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setStyleName("postTitle");

		// if the user chose to specify a picture url, use it
		// otherwise use the default images
		if (post.getPicture().equals("")) {
			postItem.setWidget(0, 0, new Image(getDefaultTypeImageUrl(post.getType())));
		} else {
			Image img = new Image(post.getPicture());
			img.setSize("34px", "34px");
			postItem.setWidget(0, 0, img);
		}
		postItem.setWidget(0, 1, titleLabel);
		postItem.setText(0, 2, post.getDescription());
		
		postItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				postItemExpand(postItem, post);
				try {
					selectedPost = postsTable.getCellForEvent(event).getRowIndex();
				} catch (Exception e) {
				}
				compressNonSelectedPostItems();
			}
		});
		return postItem;
	}
	
	// show more info about the selected post
	// shows poster, text and date as well as a delete button for moderators
	private void postItemExpand(FlexTable postItem, Post post) {
        
		postItem.getCellFormatter().setAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		postItem.getColumnFormatter().setWidth(2, "55%");
		postItem.getColumnFormatter().setWidth(3, "20%");
		postItem.setText(0, 3, "by: " + post.getAuthor());
		postItem.setText(1, 2, post.getText());
		postItem.getCellFormatter().setAlignment(1, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_BOTTOM);
		postItem.setText(1, 3, post.getDate().toString());
		
		// if the current user is a admin, show the remove button
        async.getCurrentMyUser(checkUserCallback);
        if (userIsAdmin) {
        	Button removeButton = makeRemoveButton(post.getId());
        	
        	postItem.setWidget(0, 4, removeButton);
        }
		// if the current user is the author of the selected post or if he
        // is a admin, show the update button
        if (currentUser == post.getAuthor() || userIsAdmin) {
        	Button updateButton = makeUpdateButton(post);
        	
    		postItem.getColumnFormatter().setWidth(2, "50%");
    		postItem.getColumnFormatter().setWidth(4, "5%");
        	postItem.setWidget(1, 4, updateButton);
        }
	}
	
	private Button makeUpdateButton(final Post post) {
		Button b = new Button();
		b.setSize("17px", "17px");
		b.setStyleName("updateButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				editPostDialog(post);
			}
		});
		return b;
	}

	private Button makeRemoveButton(final Long postId) {
		Button b = new Button();
		b.setSize("17px", "17px");
		b.setStyleName("removeButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				async.deletePost(postId, 
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("PostsPanel.deletePost failed \n"
										+ caught);
							}
							@Override
							public void onSuccess(Void result) {
								initPosts();
							}
						});
			}
		});
		return b;
	}

	private void compressNonSelectedPostItems() {
		int row = 0;
		for (int i = 0; i<postList.size(); i++) {
			FlexTable p = (FlexTable) postsTable.getWidget(i, 0);
        	if (row != selectedPost) { //compress all except for selected
        		p.getCellFormatter().setAlignment(0, 1, 
        				HasHorizontalAlignment.ALIGN_LEFT,
        				HasVerticalAlignment.ALIGN_MIDDLE);
        		try {
        			p.removeCell(0, 3);
        		} catch (Exception e) {
        		} try {
        			p.removeRow(1);
        		} catch (Exception e) {    			
        		} try {
        			p.removeCell(0, 4);        			
        		} catch (Exception e) {    			
        		} try {
        			p.removeCell(1, 4);        			
        		} catch (Exception e) {   			
        		}
        		p.getColumnFormatter().setWidth(2, "75%");
			}
        	row++;
        }
	}
	
	private String getDefaultTypeImageUrl(String type) {
		if (defaultTypeImageUrls.containsKey(type))
		    return defaultTypeImageUrls.get(type);
		else 
			return defaultTypeImageUrls.get("error");
	}
		
	public void showPostList(String filterBy, ArrayList<String> filter) {
		async.fetchPosts(filterBy, filter,
            new AsyncCallback<ArrayList<Post>>() {
		        @Override
		        public void onFailure(Throwable caught) {
		      	  Window.alert("makePostList(String filterBy).fetchPosts failed \n"
		                  + caught);
		        }
				@Override
		        public void onSuccess(ArrayList<Post> result) {
		        	updatePostList(result);
		        }
		});
	}

	private void editPostDialog(Post post) {
		NewEditDialog newEditDialog = new NewEditDialog(post);
		newEditDialog.show();
		newEditDialog.center();		
		newEditDialog.addCloseHandler(dialogCloseHandler);
	}
	
	public void newPostDialog() {
		newPostDialog.show();
		newPostDialog.center();
		newPostDialog.addCloseHandler(dialogCloseHandler);
	}
}