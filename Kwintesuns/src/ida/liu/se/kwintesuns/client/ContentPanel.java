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
import com.google.youtube.client.YouTubeEmbeddedPlayer;

public class ContentPanel extends FlexTable {

	private ScrollPanel commentPanel = new ScrollPanel();
	private ScrollPanel postsPanel = new ScrollPanel();
	private FlexTable commentsTable = new FlexTable();
	private FlexTable commentContents = new FlexTable();
	private WatermarkedTextArea newCommentTextArea = new WatermarkedTextArea();
	private YouTubeEmbeddedPlayer youTubePlayer;
	private ArrayList<Post> postList;
	private int selectedPostNr = 0;
	private Post selectedPost;
	private MyUser currentUser;
	private boolean userIsAdmin = false;
	private FlexTable postsTable = new FlexTable();
	private NewPostDialog newPostDialog = null;
	private EditPostDialog editPostDialog = null;
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
			Window.alert("PostsPanel.checkUserCallback " +
					"failed \n" + caught);
		}
		@Override
		public void onSuccess(MyUser result) {
			if (result != null) {
				userIsAdmin = result.isAdministrator();
				currentUser = result;
			}
		}
    };
	
	public ContentPanel() {
		
		this.newPostDialog = new NewPostDialog();
		this.newPostDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// If a new post was stored
				if (newPostDialog.getNewPostId() != null) {
					// Adds a temporary post to skip another servercall
					Post p = newPostDialog.getTextBoxValues();
					p.setId(newPostDialog.getNewPostId());
					p.setAuthor(currentUser.getEmail());
					p.setDate(new Date());
					postList.add(0, p);
					updatePostList(postList);
				} else {
					((DisclosurePanel) postsTable.getWidget(selectedPostNr, 0))
	    				.setOpen(true);
				}
			}
		});

		postsTable.setSize("100%", "100%");
		postsPanel.setSize("100%", "100%");
		postsPanel.add(postsTable);	
		postsPanel.setStyleName("postPanel");
		
		commentPanel();
		
		getFlexCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		getColumnFormatter().setWidth(0, "50%");
		
		setWidget(0, 0, postsPanel);
		setWidget(0, 1, commentPanel);
		
		setSize("100%", "100%");
	}
	
	private void commentPanel() {
		
		Button updateCommentsButton = new Button();
		updateCommentsButton.setSize("16px", "16px");
		updateCommentsButton.setStyleName("updateButton");
		updateCommentsButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				showComments(selectedPost.getId());
			}
		});
		
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
		        		async.storeComment(s, selectedPost.getId(), new AsyncCallback<Long>() {
						    @Override
						    public void onFailure(Throwable caught) {
						        Window.alert(
						        		"newPost().storePost failed \n" + caught);
						    }
						    @Override
						    public void onSuccess(Long result) {
						    	if (result == null) {
						    		Window.alert("newPost().storePost failed \n" +
						    				"result is null.");
						    	} else {
						    		newCommentTextArea.setText("");
						    		selectedPost.setId(result);
						    		showComments(result);
						    	}
						    }
						});
		        	} else {
		        		Window.alert("Too long comment (Max 300 characters).");
		        	}
		        }
		    }
		});
		
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
		
		commentPanel.add(commentContents);
	}
	
	/**
	 * Get all the posts
	 */
	public void init() {
		
		async.getAllPosts(new AsyncCallback<ArrayList<Post>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("PostsPanel.initPosts.getAllPosts " +
						"failed \n" + caught);
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
	public void showPostList(String filterBy, final ArrayList<String> filter) {
		
		async.fetchPosts(filterBy, filter,
            new AsyncCallback<ArrayList<Post>>() {
		        @Override
		        public void onFailure(Throwable caught) {
		      	  Window.alert("PostsPanel.showPostList." +
		      	  		"fetchPosts failed \n"
		                  + caught);
		      	  throw new UnsupportedOperationException("Not supported yet.");
		        }
				@Override
		        public void onSuccess(ArrayList<Post> result) {
					// if we use multiple filters in the datastore query
					// each filter will create it's own query and afterwards
					// the results from these queries will need to be sorted.
					if (filter.size() > 1) {
						Quicksort sorter = new Quicksort();
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
				selectedPostNr = row;
				selectedPost = post;
				compressNonSelectedPostItems();
				// Show the comments for the selected post
				showComments(post.getId());
			}
		});
		
		postItem.setHeader(makePostHeaderItem(post));
		postItem.setContent(makePostContentItem(post));
		
		return postItem;
	}
	
	/** 
	 * The info which is displayed by default
	 * shows picture, title and description
	 * @param post the current post from which we get the info
	 * @return the headerItem
	 */
	private FlexTable makePostHeaderItem(Post post) {
		
		FlexTable headerItem = new FlexTable();
		headerItem.getColumnFormatter().setWidth(1, "25%");
		headerItem.getColumnFormatter().setWidth(2, "75%");
				
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setStyleName("postTitle");
		Label descriptionLabel = new Label(post.getDescription());
		descriptionLabel.setStyleName("postDescription");

		// If the user chose to specify a picture url, use it
		// otherwise use the default images
		final Image thumbnail;
		if (post.getPicture().equals("")) {
			thumbnail = new Image(getDefaultTypeImageUrl(post.getType()));
		} else {
			thumbnail = new Image(post.getPicture());
			thumbnail.setSize("34px", "34px");
		}
		headerItem.setWidget(0, 0, thumbnail);
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
	private FlexTable makePostContentItem(final Post post) {

		FlexTable contentItem = new FlexTable();
		Label updateLabel = new Label(post.getUpdate());
		Label dateLabel = new Label(DateTimeFormat
				.getFormat("yyyy-MM-dd HH:mm:ss").format(post.getDate()));
		
		updateLabel.setStyleName("postSmall");
		dateLabel.setStyleName("postSmall");
		
		contentItem.getCellFormatter().setAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		contentItem.getColumnFormatter().setWidth(2, "80%");
		contentItem.getColumnFormatter().setWidth(3, "20%");
		
		contentItem.setWidget(0, 3, fixSubscribeLabel(post.getAuthor()));

		// if its a video, parse the string for the videoId 
		// and add a embedded YouTube player for that video
		if (post.getType().equals("video"))
			contentItem.setWidget(0, 2, fixVideo(post));
		
		// if its a picture add it to the postItem
		else if (post.getType().equals("picture"))
			contentItem.setWidget(0, 2, fixPicture(post));
		
		// else just add the text to the postItem
		else
			contentItem.setText(0, 2, post.getText());
		
		contentItem.setWidget(1, 2, updateLabel);		
		contentItem.getCellFormatter().setAlignment(1, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_BOTTOM);
		contentItem.setWidget(1, 3, dateLabel);
		
		// if the current user is a admin, show the remove button
        if (userIsAdmin) {
        	contentItem.getCellFormatter().setAlignment(0, 4, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_TOP);        	
        	contentItem.setWidget(0, 4, makeRemovePostButton(post.getId()));
        }
		// if the current user is the author of the selected post or if he
        // is a admin, show the update button
        if ((currentUser != null) && 
        	(currentUser.getEmail().equals(post.getAuthor()) || userIsAdmin)) {
        	contentItem.getCellFormatter().setAlignment(1, 4, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_BOTTOM);    		
        	contentItem.setWidget(1, 4, makeUpdatePostButton(post));
        }
        contentItem.setWidth("100%");
        
        return contentItem;
	}
	
	/**
	 * Checks if the email is in the subscription list of the current user
	 * @param email of the user to subscribe/unsubscribe to
	 * @return is the email in the subscription list?
	 */
	protected boolean subscribedTo(String email) {
		return currentUser.getSubscriptionList().contains(email);
	}

	/**
	 * Create a image
	 * @param post used to get the url for the picture
	 * @return the image widget
	 */
	private Image fixPicture(final Post post) {
		final Image img = new Image(post.getText());
		if (img.getHeight() != 0) {
			final int w = img.getWidth();
			// aspectRatio is used to keep the same ratio between
			// height and width when scaling
			final float aspectRatio = (float) w / 
					(float) img.getHeight();
			// when the image is loaded scale it to match the layout
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
		// when the image is clicked open a new tab with
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
	private YouTubeEmbeddedPlayer fixVideo(Post post) {
		String videoId = null;
		String[] split = post.getText().split("v=");
		if (split.length >= 2)
			videoId = split[1];
		youTubePlayer = new YouTubeEmbeddedPlayer(videoId);
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
	private Label fixSubscribeLabel(final String author) {
		final PopupPanel subPopup = new PopupPanel();
		subPopup.setAutoHideEnabled(true);
		final Label authorLabel = new Label("by: " + author);
		
		// unsubscribe/subscribe button
		authorLabel.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if (!subscribedTo(author)) {
					// subscribe if the user isn't subscribed to this yet
					async.subscribe(author, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("makePostContentItem.subscribe" +
									"failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
							subPopup.setWidget(new Label("You are now subscribed to " 
									+ author));
							subPopup.showRelativeTo(authorLabel);
						}
					});
				} else {
					// unsubscribe if the user is subscribed to this
					async.unsubscribe(author, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("makePostContentItem.unSubscribe" +
									"failed \n" + caught);
						}
						@Override
						public void onSuccess(Void result) {
							subPopup.setWidget(new Label("You are no longer subscribed to " 
									+ author));
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
	 * Loop through all postItems and compress
	 */
	private void compressNonSelectedPostItems() {
		
		for (int row = 0; row < postList.size(); row++) {
			// compress all except for selected
        	if ((row != selectedPostNr) && postsTable.isCellPresent(row, 0)) {
        		((DisclosurePanel) postsTable.getWidget(row, 0))
        			.setOpen(false);
			} else {
				// don't compress the selected post
			}
        }
	}
	/**
	 * Get all the comments for a post
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
	 * Update the commentsTable to refresh the displayed comments
	 * @param commentList the updated list containing the comments to display
	 */
	private void updateCommentList(ArrayList<Comment> commentList) {
		
        commentsTable.removeAllRows();
		if (!commentList.isEmpty()) {
	        //loop the array list and comment getters to add 
	        //records to the table
			int row = 0;
	        for (Comment comment : commentList) {
	        	row = commentsTable.getRowCount();
	        	commentsTable.setWidget(row, 0,
	        			newCommentItem(comment));
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
		commentItem.setStyleName("commentItem");
		commentItem.setWidth("100%");
		commentItem.getColumnFormatter().setWidth(0, "80%");
		commentItem.getColumnFormatter().setWidth(1, "20%");

		Label authorLabel = new Label("by: " + comment.getAuthor());
		Label dateLabel = new Label(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
				.format(comment.getDate()));
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
		
		// if the user is a admin, show the remove comment button
		if (userIsAdmin) {
    		Button removeButton = makeRemoveCommentButton(comment.getId());
        	commentItem.getCellFormatter().setAlignment(0, 2, 
    				HasHorizontalAlignment.ALIGN_RIGHT,
    				HasVerticalAlignment.ALIGN_TOP);
        	
        	commentItem.setWidget(0, 2, removeButton);
		}
		
		return commentItem;
	}
	
	/**
	 * Shows the dialog for making a new post
	 */
	public void newPostDialog() {
		// if there is any posts in the postsTable, compress the selected
		// one while the newpost dialog is up.
		try {
			((DisclosurePanel) postsTable.getWidget(selectedPostNr, 0))
				.setOpen(false);
		} catch (IndexOutOfBoundsException e) {
			// there are no posts
		}
		newPostDialog.center();
	}

	/**
	 * Shows the dialog for editing a post
	 * @param post is needed to determine old values
	 */
	private void editPostDialog(Post post) {
		editPostDialog = new EditPostDialog(post, currentUser.getEmail());
		editPostDialog.center();
		editPostDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				init();
			}
		});
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
	private Button makeUpdatePostButton(final Post post) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("editButton");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
		return b;
	}

	/**
	 * Creates a button used to remove a post (for moderators)
	 * @param postId needed for the call to deletePost
	 * @return the remove button
	 */
	private Button makeRemovePostButton(final Long postId) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("removeButton");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (postId != null) {
					async.deletePost(postId, 
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("PostsPanel.makeRemoveButton." +
											"deletePost failed \n" + caught);
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
		return b;
	}
	
	/**
	 * Creates a button used to remove a comment (for moderators)
	 * @param commentId needed for the call to deleteComment
	 * @return the remove button
	 */
	private Button makeRemoveCommentButton(final Long commentId) {
		
		Button b = new Button();
		b.setSize("16px", "16px");
		b.setStyleName("removeCommentButton");
		b.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				async.deleteComment(commentId, 
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("CommentPanel.deleteComment failed \n"
										+ caught);
							}
							@Override
							public void onSuccess(Void result) {
								showComments(selectedPost.getId());
							}
						});
			}
		});
		return b;
	}
}