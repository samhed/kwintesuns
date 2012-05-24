package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.youtube.client.YouTubeEmbeddedPlayer;

public class ContentPanel extends FlexTable {

	private final ServerServiceAsync async = GWT.create(ServerService.class);
	private FlexTable commentsTable = new FlexTable();
	private FlexTable postsTable = new FlexTable();
	private ArrayList<Post> postList;
	private int selectedPostNr = 0;
	private Post selectedPost;
	private MyUser currentUser;
	private boolean userIsAdmin = false;

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
	
    /**
     * The panel containing the posts and the comments.
     */
	public ContentPanel() {
		
		getFlexCellFormatter().setAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		getColumnFormatter().setWidth(0, "50%");
		
		setWidget(0, 0, postPanel());
		setWidget(0, 1, commentPanel());
		
		setSize("100%", "100%");
	}
	
	/**
	 * The panel containing the posts.
	 * @return the postPanel.
	 */	
	private ScrollPanel postPanel() {
		
		// The postsTable contains all the posts.
		postsTable.setSize("100%", "100%");
		
		// The postPanel is a ScrollPanel containing the postTable.
		ScrollPanel postPanel = new ScrollPanel();
		postPanel.setSize("100%", "100%");
		postPanel.add(postsTable);	
		postPanel.setStyleName("postPanel");
		
		return postPanel;
	}
	
	/**
	 * The panel containing the comments.
	 * @return the commentPanel.
	 */
	private ScrollPanel commentPanel() {
		
		// The updateCommentsButton is used to refresh 
		// the list of comments for the selected post.
		Button updateCommentsButton = new Button();
		updateCommentsButton.setSize("16px", "16px");
		updateCommentsButton.setStyleName("refreshButton");
		updateCommentsButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				showComments(selectedPost.getId());
			}
		});
		
		// The newCommentTextArea is the field (TextArea)
		// where the users write a new comment.
		final WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
		newCommentTextArea.setWidth("95%");
		newCommentTextArea.setWatermark("Write a new comment (Max 300 characters)");
		newCommentTextArea.setCharLimit(300); //for firefox
		newCommentTextArea.getElement().setAttribute("maxlength", "300"); //for chrome
		newCommentTextArea.addKeyDownHandler(new KeyDownHandler() {
		    @Override
		    public void onKeyDown(KeyDownEvent event) {
		        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		        	if (newCommentTextArea.isWithinLimit()) {
		        		String s = newCommentTextArea.getText();
				    	newCommentTextArea.setText("");
		        		async.storeComment(s, selectedPost.getId(), new AsyncCallback<Void>() {
						    @Override
						    public void onFailure(Throwable caught) {
						        Window.alert("newPost().storePost failed \n" + caught);
						    }
						    @Override
						    public void onSuccess(Void result) {
					    		newCommentTextArea.setText("");
						    	showComments(selectedPost.getId());
						    }
						});
		        	} else {
		        		Window.alert("Too long comment (Max 300 characters).");
		        	}
		        }
		    }
		});
		
		// The newCommentBox contains:
		// * the textbox where the users write new comments
		// * the updateCommentsButton
		FlexTable newCommentBox = new FlexTable();
		newCommentBox.setSize("100%", "100%");
		newCommentBox.getColumnFormatter().setWidth(0, "*");
		newCommentBox.getColumnFormatter().setWidth(1, "18px");
		newCommentBox.getCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_CENTER, 
				HasVerticalAlignment.ALIGN_TOP);
		newCommentBox.setWidget(0, 0, newCommentTextArea);
		newCommentBox.getCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_CENTER, 
				HasVerticalAlignment.ALIGN_MIDDLE);
		newCommentBox.setWidget(0, 1, updateCommentsButton);
		
		commentsTable.setSize("100%", "100%");

		// The commentContents contains:
		// * the newCommentBox
		// * the commentsTable
		FlexTable commentContents = new FlexTable();
		commentContents.setSize("100%", "100%");
		commentContents.getCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_CENTER, 
				HasVerticalAlignment.ALIGN_TOP);
		commentContents.setWidget(0, 0, newCommentBox);
		commentContents.getCellFormatter().setAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		commentContents.setWidget(1, 0, commentsTable);
		commentContents.setStyleName("commentPanel");
		
		// The commentPanel is a ScrollPanel containing 
		// the commentContents panel.
		ScrollPanel commentPanel = new ScrollPanel();
		commentPanel.add(commentContents);
		
		return commentPanel;
	}
	
	/**
	 * Initialize the ContentPanel by getting all the posts
	 * and getting the info about the current user.
	 */
	public void init() {
		async.getAllPosts(new AsyncCallback<ArrayList<Post>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("ContentPanel.init.getAllPosts failed \n" + caught);
			}
			@Override
			public void onSuccess(ArrayList<Post> result) {
				updatePostList(result);
			}
		});
        async.getCurrentMyUser(new AsyncCallback<MyUser>() {
    		@Override
    		public void onFailure(Throwable caught) {
    			Window.alert("ContentPanel.init.getCurrentMyUser failed \n" + caught);
    		}
    		@Override
    		public void onSuccess(MyUser result) {
    			if (result != null) {
    		    	// userIsAdmin and currentUser is used when checking whether 
    		    	// to display the remove and edit buttons
    				userIsAdmin = result.isAdministrator();
    				currentUser = result;
    			}
    		}
        });
	}
	
	/***************************************************************************************
	*									Post functions
	***************************************************************************************/
	
	/**
	 * Get all the posts to match the filter
	 * @param filterBy which member to filter by
	 * @param filter list of strings to filter with
	 */
	public void showPostList(String filterBy, final ArrayList<String> filter) {		
		async.fetchPosts(filterBy, filter,
            new AsyncCallback<ArrayList<Post>>() {
		        @Override
		        public void onFailure(Throwable caught) {
		      	  Window.alert("PostsPanel.showPostList.fetchPosts failed \n" + caught);
		      	  throw new UnsupportedOperationException("Not supported yet.");
		        }
				@Override
		        public void onSuccess(ArrayList<Post> result) {
					// If we use multiple filters in the datastore query
					// each filter will create it's own query and afterwards
					// the results from these queries will need to be sorted.
					if (filter.size() > 1) {
						PostQuicksort sorter = new PostQuicksort();
						sorter.sort(result);
					}
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
	        // Loop through the postList and add a postItem 
	        // for each post to the postsTable.
			int row = 0;
	        for (Post post : postList) {
	        	row = postsTable.getRowCount();
	        	postsTable.setWidget(row, 0, newPostItem(post, row));
	        }
	        DisclosurePanel first = (DisclosurePanel) postsTable.getWidget(0, 0);
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
				selectedPostNr = row;
				selectedPost = post;
				compressNonSelectedPostItems();
				// Show the comments for the selected post
				showComments(post.getId());
			}
		});
		
		postItem.setHeader(postHeader(post));
		postItem.setContent(postContent(post));
		
		return postItem;
	}
	
	/** 
	 * Loop through all postItems and compress where needed.
	 */
	private void compressNonSelectedPostItems() {		
		for (int row = 0; row < postList.size(); row++) {
			// Compress all except for selected post, else don't.
        	if ((row != selectedPostNr) && postsTable.isCellPresent(row, 0))
        		((DisclosurePanel) postsTable.getWidget(row, 0)).setOpen(false);
        }
	}
	
	/** 
	 * The info which is displayed by default
	 * shows picture, title and description
	 * @param post the current post from which we get the info
	 * @return the headerItem
	 */
	private FlexTable postHeader(Post post) {
		
		FlexTable postHeaderTable = new FlexTable();
		postHeaderTable.getColumnFormatter().setWidth(1, "25%");
		postHeaderTable.getColumnFormatter().setWidth(2, "75%");
				
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setStyleName("postTitle");
		Label descriptionLabel = new Label(post.getDescription());
		descriptionLabel.setStyleName("postDescription");

		// If the user chose to specify a thumbnail url, use it
		// otherwise use the default images
		final Image thumbnail;
		if (post.getThumbnail().equals("")) {
			thumbnail = new Image(getDefaultTypeImageUrl(post.getType()));
		} else {
			thumbnail = new Image(post.getThumbnail());
			thumbnail.setSize("34px", "34px");
		}
		postHeaderTable.setWidget(0, 0, thumbnail);
		postHeaderTable.setWidget(0, 1, titleLabel);
		postHeaderTable.setWidget(0, 2, descriptionLabel);
		postHeaderTable.setWidth("100%");
		
		return postHeaderTable;
	}
	
	/**
	 * Parse the mapped default type images to find the
	 * one matching type.
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
	 * More info about the selected post
	 * shows poster, text and date as well as a delete button for admins
	 * @param post the current post from which we get the info
	 * @return the contentItem
	 */
	private FlexTable postContent(final Post post) {

		FlexTable postContentTable = new FlexTable();
		// A small panel containing the remove and the flag buttons.
		VerticalPanel postButtonPanel = new VerticalPanel();		
		postButtonPanel.setWidth("16px");
		
		Label updateLabel = new Label(post.getUpdate());
		Label dateLabel = new Label(DateTimeFormat
				.getFormat("yyyy-MM-dd HH:mm:ss").format(post.getDate()));
		
		updateLabel.setStyleName("postSmall");
		dateLabel.setStyleName("postSmall");
		
		postContentTable.getCellFormatter().setAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		postContentTable.getColumnFormatter().setWidth(2, "80%");
		postContentTable.getColumnFormatter().setWidth(3, "20%");
		
		postContentTable.setWidget(0, 3, makeSubscribeLabel(post.getAuthor()));

		// if its a video, parse the string for the videoId 
		// and add a embedded YouTube player for that video
		if (post.getType().equals("video"))
			postContentTable.setWidget(0, 2, makeEmbeddedVideoPlayer(post));
		
		// if its a picture add it to the postItem
		else if (post.getType().equals("picture"))
			postContentTable.setWidget(0, 2, makeEmbeddedPicture(post));
		
		// else just add the text to the postItem
		else
			postContentTable.setText(0, 2, post.getText());
		
		postContentTable.setWidget(1, 2, updateLabel);		
		postContentTable.getCellFormatter().setAlignment(1, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_BOTTOM);
		postContentTable.setWidget(1, 3, dateLabel);
		
		// Add the flag post button for all users, 
		// even if you are not logged in.
		postButtonPanel.add(makeFlagPostButton(post.getId()));
		
		// if the current user is a admin, show the remove button
        if (userIsAdmin)
        	postButtonPanel.add(makeRemovePostButton(post.getId()));

    	postContentTable.getCellFormatter().setAlignment(0, 4, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
    	postContentTable.setWidget(0, 4, postButtonPanel);
    	
		// if the current user is the author of the selected post or if he
        // is a admin, show the update button
        if ((currentUser != null) && 
        	(currentUser.getEmail().equals(post.getAuthor()) || userIsAdmin)) {
        	postContentTable.getCellFormatter().setAlignment(1, 4, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_BOTTOM);    		
        	postContentTable.setWidget(1, 4, makeUpdatePostButton(post));
        }
        postContentTable.setWidth("100%");
        
        return postContentTable;
	}
	
	/***************************************************************************************
	*									Comment functions
	***************************************************************************************/
	
	/**
	 * Get all the comments for a post.
	 * @param post which post to show comments for
	 */
	public void showComments(Long postId) {		
		async.getComments(postId, new AsyncCallback<ArrayList<Comment>>() {
			@Override
			public void onFailure(Throwable caught) {
        		Window.alert("showComment.getComments failed \n" + caught);
			}
			@Override
			public void onSuccess(ArrayList<Comment> result) {
	        	updateCommentList(result);
			}
		});
	}
	
	/**
	 * Update the commentsTable to refresh the displayed comments.
	 * @param commentList the updated list containing the comments to display
	 */
	private void updateCommentList(ArrayList<Comment> commentList) {		
        commentsTable.removeAllRows();
		if (!commentList.isEmpty()) {
	        // Loop the array list and comment getters to add 
	        // records to the table.
			int row = 0;
	        for (Comment comment : commentList) {
	        	row = commentsTable.getRowCount();
	        	commentsTable.setWidget(row, 0, newCommentItem(comment));
	        }
		}
	}
	
	/**
	 * Creates a new comment item to be displayed in the commentsTable
	 * @param comment the current comment from which we get the info
	 * @return the new comment item
	 */
	private FlexTable newCommentItem(Comment comment) {
		
		FlexTable commentItem = new FlexTable();

		// A small panel containing the remove and the flag buttons.
		VerticalPanel commentButtonPanel = new VerticalPanel();		
		commentButtonPanel.setSize("16px", "32px");
		
		commentItem.setStyleName("commentItem");
		commentItem.setWidth("100%");
		commentItem.getColumnFormatter().setWidth(0, "80%");
		commentItem.getColumnFormatter().setWidth(1, "20%");

		Label authorLabel = new Label("by: " + comment.getAuthor());
		Label dateLabel = new Label(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(comment.getDate()));
		authorLabel.setStyleName("postSmall");
		dateLabel.setStyleName("postSmall");

		commentItem.setText(0, 0, comment.getText());
		
		commentItem.getCellFormatter().setAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		commentItem.getCellFormatter().setAlignment(1, 1,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);

		commentItem.setWidget(0, 1, authorLabel);
		commentItem.setWidget(1, 1, dateLabel);
		
		commentButtonPanel.add(makeFlagCommentButton(comment.getId()));
		
		// If the user is a admin, show the remove comment button
		if (userIsAdmin)
			commentButtonPanel.add(makeRemoveCommentButton(comment.getId()));
		
    	commentItem.getCellFormatter().setAlignment(0, 2, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
    	
    	commentItem.setWidget(0, 2, commentButtonPanel);
		
		return commentItem;
	}
	
	/***************************************************************************************
	*										PostDialogs
	***************************************************************************************/
	
	/**
	 * Creates the dialog for making a new post.
	 */
	public void newPostDialog() {		
		// If there is any posts in the postsTable, compress the selected
		// one while the newpost dialog is up.
		try {
			((DisclosurePanel) postsTable.getWidget(selectedPostNr, 0)).setOpen(false);
		} catch (IndexOutOfBoundsException e) {
			// There are no posts
		}
		
		final NewPostDialog newPostDialog = new NewPostDialog();
		newPostDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// If a new post was stored
				if (newPostDialog.getNewPostId() != null) {
					// Adds a temporary post to skip another servercall
					Post p = newPostDialog.getTextBoxValues();
					p.setId(newPostDialog.getNewPostId());
					
					String author;
					if (currentUser == null)
						author = "Anonymous";
					else 
						author = currentUser.getEmail();
					
					p.setAuthor(author);
					p.setDate(new Date());
					postList.add(0, p);
					updatePostList(postList);
				} else {
					((DisclosurePanel) postsTable.getWidget(selectedPostNr, 0)).setOpen(true);
				}
			}
		});		
		newPostDialog.center();
	}

	/**
	 * Creates the dialog for editing a post.
	 * @param post is needed to determine old values
	 */
	private void editPostDialog(Post post) {
		// Compress the selected post while the editpost dialog is up.		
		((DisclosurePanel) postsTable.getWidget(selectedPostNr, 0)).setOpen(false);
		
		String author;
		if (currentUser == null)
			author = "Anonymous";
		else 
			author = currentUser.getEmail();
		
		EditPostDialog editPostDialog = new EditPostDialog(post, author, currentUser.isAdministrator());
		editPostDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				init();
			}
		});
		editPostDialog.center();
	}
		
	/***************************************************************************************
	*										Makers 
	*			(functions for constructing smaller parts of the ContentPanel)
	***************************************************************************************/
	
	/**
	 * Create a image for a post with the type "picture".
	 * @param post used to get the url for the picture
	 * @return the image widget
	 */
	private Image makeEmbeddedPicture(final Post post) {
		final Image img = new Image(post.getText());
		if (img.getHeight() != 0) {
			final int w = img.getWidth();
			// AspectRatio is used to keep the same ratio between
			// height and width when scaling
			final float aspectRatio = (float) w / (float) img.getHeight();
			// When the image is loaded scale it to match the layout
			img.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					img.setWidth("427px");
					if (w > 427)
						img.setHeight((int) (427/aspectRatio) + "px");
					else if (w <= 427)
						img.setHeight((int) (427*aspectRatio) + "px");
				}
			});
		}
		// When the image is clicked open a new tab with
		// the picture url.
		img.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				Window.open(post.getText(), post.getTitle(), "");
			}
		});
		return img;
	}
	
	/**
	 * Create a embedded youtube player
	 * @param post used to parse the string for the videoId
	 * @return the youtube player widget
	 */
	private YouTubeEmbeddedPlayer makeEmbeddedVideoPlayer(Post post) {
		String videoId = null;
		// Split the string when it finds "v=" because
		// after that comes the videoId.
		String[] split = post.getText().split("v=");
		if (split.length >= 2)
			videoId = split[1];
		YouTubeEmbeddedPlayer youTubePlayer = new YouTubeEmbeddedPlayer(videoId);
		youTubePlayer.setSize("427px", "320px");
		youTubePlayer.embed();
		youTubePlayer.setFullScreen(true);
		
		return youTubePlayer;
	}
	
	/**
	 * Create a label displaying the author of the post
	 * that when clicked adds or removes that author from
	 * the current users subscription list.
	 * @param author of the post
	 * @return the label
	 */
	private Label makeSubscribeLabel(final String author) {
		final PopupPanel subPopup = new PopupPanel();
		subPopup.setAutoHideEnabled(true);
		final Label authorLabel = new Label("by: " + author);
		
		// Unsubscribe/subscribe button
		authorLabel.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				boolean isSubscribed;
				try {
					isSubscribed = !currentUser.getSubscriptionList().contains(author);
				} catch (NullPointerException e) {
					return; // The user is not logged in don't do anything.
				}
				if (isSubscribed) {
					// Subscribe if the user isn't subscribed to this yet
					async.subscribe(author, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("makeSubscribeLabel.subscribe failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
							subPopup.setWidget(new Label("You are now subscribed to " + author));
							subPopup.showRelativeTo(authorLabel);
						}
					});
				} else {
					// Unsubscribe if the user is subscribed to this
					async.unsubscribe(author, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("makeSubscribeLabel.unSubscribe failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
							subPopup.setWidget(new Label("You are no longer subscribed to " + author));
							subPopup.showRelativeTo(authorLabel);
						}
					});
				}
			}
		});
		authorLabel.setStyleName("postSmall");
		return authorLabel;
	}

	/**
	 * Creates a button used to flag a post.
	 * Anonymous users will share one button(vote),
	 * logged in users will get one each. 
	 * @param postId needed for the call to flag post
	 * @return the flag post button
	 */
	private Button makeFlagPostButton(final Long postId) {
		final PopupPanel flagPopup = new PopupPanel();
		flagPopup.setAutoHideEnabled(true);
		
		final Button flagPostButton = new Button();
		flagPostButton.setSize("16px", "16px");
		flagPostButton.setStyleName("flagButton");
		flagPostButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (postId != null) {
					String flagger;
					// If there is no user logged in set the flagger
					// to Anonymous.
					if (currentUser == null)
						flagger = "Anonymous";
					else 
						flagger = currentUser.getEmail();
					
					async.flagPost(postId, flagger, new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("makeFlagPostButton.flagPost failed \n" + caught);
						}
						@Override
						public void onSuccess(Boolean result) {
							if (result)
								flagPopup.setWidget(new Label("You have now flagged this post as offensive."));
							else
								flagPopup.setWidget(new Label("You have already flagged this post."));
							flagPopup.showRelativeTo(flagPostButton);
						}
					});
				} else {
					// The post is just temporary
					Window.alert("Trying to flag a post before it was " +
							"fetched from the database, reloading page.");
					init();
				}
			}
		});
		return flagPostButton;
	}
	
	/**
	 * Creates a button used to update/edit a post.
	 * @param post needed for the call to {@link EditPostDialog}
	 * @return the update button
	 */
	private Button makeUpdatePostButton(final Post post) {
		Button updatePostButton = new Button();
		updatePostButton.setSize("16px", "16px");
		updatePostButton.setStyleName("editButton");
		updatePostButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Check if the post is temporary
				if (post.getId() != null) {
					editPostDialog(post);
				} else {
					// The post is just temporary
					Window.alert("Trying to edit a post before it was " +
							"fetched from the database, reloading page.");
					init();
				}
			}
		});
		return updatePostButton;
	}

	/**
	 * Creates a button used to remove a post (for admins).
	 * @param postId needed for the call to deletePost
	 * @return the remove button
	 */
	private Button makeRemovePostButton(final Long postId) {
		Button removePostButton = new Button();
		removePostButton.setSize("16px", "16px");
		removePostButton.setStyleName("removeButton");
		removePostButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Check if the post is temporary
				if (postId != null) {
					async.deletePost(postId, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("PostsPanel.makeRemoveButton.deletePost failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
							init();
						}
					});
				} else {
					// The post is just temporary
					Window.alert("Trying to remove a post before it was " +
							"fetched from the database, reloading page.");
					init();
				}
			}
		});
		return removePostButton;
	}
	
	/**
	 * Creates a button used to remove a comment (for admins)
	 * @param commentId needed for the call to deleteComment
	 * @return the remove button
	 */
	private Button makeRemoveCommentButton(final Long commentId) {		
		Button removeCommentButton = new Button();
		removeCommentButton.setSize("16px", "16px");
		removeCommentButton.setStyleName("removeCommentButton");
		removeCommentButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				async.deleteComment(commentId, 
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("CommentPanel.deleteComment failed \n" + caught);
							}
							@Override
							public void onSuccess(Void result) {
								showComments(selectedPost.getId());
							}
						});
			}
		});
		return removeCommentButton;
	}
	
	/**
	 * Creates a button used to flag a comment.
	 * Anonymous users will share one button(vote),
	 * logged in users will get one each. 
	 * @param commentId needed for the call to flag a comment
	 * @return the flag comment button
	 */
	private Button makeFlagCommentButton(final Long commentId) {
		final PopupPanel flagPopup = new PopupPanel();
		flagPopup.setAutoHideEnabled(true);
		
		final Button flagCommentButton = new Button();
		flagCommentButton.setSize("16px", "16px");
		flagCommentButton.setStyleName("flagCommentButton");
		flagCommentButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String flagger;
				// If there is no user logged in set the flagger
				// to Anonymous.
				if (currentUser == null)
					flagger = "Anonymous";
				else 
					flagger = currentUser.getEmail();
				
				async.flagComment(commentId, flagger, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("makeFlagCommentButton.flagComment failed \n" + caught);
					}
					@Override
					public void onSuccess(Boolean result) {
						if (result)
							flagPopup.setWidget(new Label("You have now flagged this comment as offensive."));
						else
							flagPopup.setWidget(new Label("You have already flagged this comment."));
						flagPopup.showRelativeTo(flagCommentButton);
					}
				});
			}
		});
		return flagCommentButton;
	}
}