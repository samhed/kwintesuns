package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PostsPanel extends ScrollPanel{

	private ArrayList<Post> postList;
	private int selectedPost;
	private String currentUser = "";
	private boolean userIsAdmin = false;
	private FlexTable postsTable = new FlexTable();
	private CommentPanel commentPanel;
	private final NewPostDialog newPostDialog = new NewPostDialog();
	private final ServerServiceAsync async = 
			GWT.create(ServerService.class);

	// mapping post types with their default image urls
	private static final Map<String, String> defaultTypeImageUrls;
	static {
		defaultTypeImageUrls = new HashMap<String, String>();
		defaultTypeImageUrls.put("video", "img/yt.jpg");
		defaultTypeImageUrls.put("picture", "img/img.jpg");
		defaultTypeImageUrls.put("news", "img/earth.png");
		defaultTypeImageUrls.put("thought", "img/thought.png");
		defaultTypeImageUrls.put("error", "img/error.png");
	}
	
	// userIsAdmin and currentUser is used when checking whether 
	// to display the remove and edit buttons
	private AsyncCallback<MyUser> checkUserCallback = 
			new AsyncCallback<MyUser>() {
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
	
    // Used for EditPostDialog and NewPostDialog
    private CloseHandler<PopupPanel> dialogCloseHandler = 
    		new CloseHandler<PopupPanel>() {
		@Override
		public void onClose(CloseEvent<PopupPanel> event) {initPosts();}
	};
	
	public PostsPanel(CommentPanel commentPanel) {
		
		this.commentPanel = commentPanel;
		postsTable.setWidth("100%");
		add(postsTable);
		
		setSize("100%", "100%");
		setStyleName("postPanel");
	}
	
	/**
	 * Get all the posts
	 */
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
	
	/**
	 * Get all the posts to match the filter
	 * @param filterBy which member to filter by
	 * @param filter list of strings to filter with
	 */
	public void showPostList(String filterBy, ArrayList<String> filter) {
		
		async.fetchPosts(filterBy, filter,
            new AsyncCallback<ArrayList<Post>>() {
		        @Override
		        public void onFailure(Throwable caught) {
		      	  Window.alert("makePostList(String filterBy)." +
		      	  		"fetchPosts failed \n"
		                  + caught);
		        }
				@Override
		        public void onSuccess(ArrayList<Post> result) {
		        	updatePostList(result);
		        }
		});
	}
	
	/**
	 * Update the postsTable to refresh the displayed posts
	 * @param postList the updated list containing the posts to display
	 */
	private void updatePostList(ArrayList<Post> postList) {
		
		this.postList = postList;
        postsTable.removeAllRows();
		if (!postList.isEmpty()) {
	        async.getCurrentMyUser(checkUserCallback);
	        //loop the array list and post getters to add 
	        //records to the table
			int row = 0;
	        for (Post post : postList) {
	        	row = postsTable.getRowCount();
	        	postsTable.setWidget(row, 0,
	        			newPostItem(post, row));
	        }
	        DisclosurePanel first = 
	        		(DisclosurePanel) postsTable.getWidget(0, 0);
	        first.setOpen(true);
		}
	}
	
	/**
	 * Creates a new post item to be displayed in the postsTable
	 * @param post the current post from which we get the info
	 * @param row the row of the current post in the postsTable 
	 * @return the new post item
	 */
	private DisclosurePanel newPostItem(final Post post, final int row) {

		DisclosurePanel postItem = new DisclosurePanel();
		postItem.setStyleName("postItem");
		postItem.setWidth("100%");
		postItem.setAnimationEnabled(true);		
		postItem.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				selectedPost = row;
				compressNonSelectedPostItems();
				// Show the comments for the selected post
				commentPanel.setUserIsAdmin(userIsAdmin);
				commentPanel.showComments(post.getId());
			}
		});
		
		postItem.setHeader(makeHeaderItem(post));
		postItem.setContent(makeContentItem(post));
		
		return postItem;
	}
	
	/** 
	 * The info which is displayed by default
	 * shows picture, title and description
	 * @param post the current post from which we get the info
	 * @return the headerItem
	 */
	private FlexTable makeHeaderItem(Post post) {
		
		FlexTable headerItem = new FlexTable();
		headerItem.getColumnFormatter().setWidth(1, "25%");
		headerItem.getColumnFormatter().setWidth(2, "75%");
				
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setStyleName("postTitle");
		Label descriptionLabel = new Label(post.getDescription());
		descriptionLabel.setStyleName("postDescription");

		// If the user chose to specify a picture url, use it
		// otherwise use the default images
		Widget imgWidget;
		if (post.getPicture().equals("")) {
			imgWidget = new Image(getDefaultTypeImageUrl(post.getType()));
		} else {
			imgWidget = new Image(post.getPicture());
			imgWidget.setSize("34px", "34px");
		}
		headerItem.setWidget(0, 0, imgWidget);
		headerItem.setWidget(0, 1, titleLabel);
		headerItem.setWidget(0, 2, descriptionLabel);
		headerItem.setWidth("100%");
		
		return headerItem;
	}
	
	/**
	 * More info about the selected post
	 * shows poster, text and date as well as a delete button for moderators
	 * @param post the current post from which we get the info
	 * @return the contentItem
	 */
	private FlexTable makeContentItem(Post post) {

		FlexTable contentItem = new FlexTable();
		Label updateLabel = new Label(post.getUpdate());
		Label dateLabel = new Label(DateTimeFormat
				.getFormat("yyyy-MM-dd HH:mm:ss").format(post.getDate()));
		Label authorLabel = new Label("by: " + post.getAuthor());
		updateLabel.setStyleName("postSmall");
		dateLabel.setStyleName("postSmall");
		authorLabel.setStyleName("postSmall");
		
		contentItem.getCellFormatter().setAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		contentItem.getColumnFormatter().setWidth(2, "80%");
		contentItem.getColumnFormatter().setWidth(3, "20%");
		
		contentItem.setWidget(0, 3, authorLabel);
		contentItem.setText(0, 2, post.getText());
		contentItem.setWidget(1, 2, updateLabel);
		
		contentItem.getCellFormatter().setAlignment(1, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_BOTTOM);
		
		contentItem.setWidget(1, 3, dateLabel);
		
		// if the current user is a admin, show the remove button
        if (userIsAdmin) {
    		Button removeButton = makeRemoveButton(post.getId());
        	contentItem.getCellFormatter().setAlignment(0, 4, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_TOP);
        	
        	contentItem.setWidget(0, 4, removeButton);
        }
		// if the current user is the author of the selected post or if he
        // is a admin, show the update button
        if (currentUser.equals(post.getAuthor()) || userIsAdmin) {
        	Button updateButton = makeUpdateButton(post);
        	contentItem.getCellFormatter().setAlignment(1, 4, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_BOTTOM);
    		
        	contentItem.setWidget(1, 4, updateButton);
        }
        contentItem.setWidth("100%");
        
        return contentItem;
	}

	/** 
	 * Loop through all postItems and compress
	 */
	private void compressNonSelectedPostItems() {
		
		for (int row = 0; row < postList.size(); row++) {
			// compress all except for selected
        	if ((row != selectedPost) && postsTable.isCellPresent(row, 0)) {
        		((DisclosurePanel) postsTable.getWidget(row, 0))
        			.setOpen(false);
			} else {
				// don't compress the selected post
			}
        }
	}
	
	/**
	 * Shows the dialog for making a new post
	 */
	public void newPostDialog() {
		newPostDialog.show();
		newPostDialog.center();
		newPostDialog.addCloseHandler(dialogCloseHandler);
	}

	/**
	 * Shows the dialog for editing a post
	 * @param post is needed to determine old values
	 */
	private void editPostDialog(Post post) {
		EditPostDialog newEditDialog = 
				new EditPostDialog(post, currentUser);
		newEditDialog.show();
		newEditDialog.center();		
		newEditDialog.addCloseHandler(dialogCloseHandler);
	}
	
	/**
	 * Parse the mapped default type images
	 * @param type the selected type
	 * @return the image url
	 */
	private String getDefaultTypeImageUrl(String type) {
		if (defaultTypeImageUrls.containsKey(type))
		    return defaultTypeImageUrls.get(type);
		else 
			return defaultTypeImageUrls.get("error");
	}
	
	/**
	 * Creates a button used to update/edit a post
	 * @param post needed for the call to {@link EditPostDialog}
	 * @return the update button
	 */
	private Button makeUpdateButton(final Post post) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("updateButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				editPostDialog(post);
			}
		});
		return b;
	}

	/**
	 * Creates a button used to remove a post (for moderators)
	 * @param postId needed for the call to deletePost
	 * @return the remove button
	 */
	private Button makeRemoveButton(final Long postId) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("removeButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				async.deletePost(postId, 
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("PostsPanel.deletePost " +
										"failed \n" + caught);
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
}