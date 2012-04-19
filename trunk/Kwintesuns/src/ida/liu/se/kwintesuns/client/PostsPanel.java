package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	private final MyUserServiceAsync async = GWT.create(MyUserService.class);
	private ArrayList<Post> postList;
	private int selectedPost;
	
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
		MyUserServiceAsync async = GWT.create(MyUserService.class);
		async.getAllPosts(new AsyncCallback<ArrayList<Post>>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("initPosts().getAllPosts failed \n"
						+ caught);
				}
				public void onSuccess(ArrayList<Post> result) {
					updatePostList(result);
				}
			});
	}
	
	private void updatePostList(ArrayList<Post> result) {
		if (!result.isEmpty()) {
			postList = result;
			int row = 0;
	        postsTable.removeAllRows();
	        //loop the array list and post getters to add 
	        //records to the table
	        for (Post post : postList) {
	        	row = postsTable.getRowCount();
	        	postsTable.setWidget(row, 0,
	        			newPostItem(post.getType(),	post.getDate(),
	        					post.getTitle(), post.getPoster(),
	        					post.getText(), post.getPicture(),
	        					post.getDescription()));
	        }
	        Post first = result.get(0);
			postItemExpand((FlexTable) postsTable.getWidget(0, 0), first.getPoster(), 
					first.getText(), first.getDate());
			selectedPost = 0;
			compressNonSelectedPostItems();
		}
	}
	
	private FlexTable newPostItem(String type, final Date date, String title, 
			final String poster, final String text, String pictureUrl, 
			String description) {

		final FlexTable p = new FlexTable();
		p.setStyleName("postItem");
		p.setWidth("100%");
		p.getColumnFormatter().setWidth(1, "25%");
		p.getColumnFormatter().setWidth(2, "75%");
				
		Label titleLabel = new Label(title);
		titleLabel.setStyleName("postTitle");

		if (pictureUrl.equals("")) {
			p.setWidget(0, 0, new Image(getDefaultTypeImageUrl(type)));
		} else {
			Image img = new Image(pictureUrl);
			img.setSize("34px", "34px");
			p.setWidget(0, 0, img);
		}
		p.setWidget(0, 1, titleLabel);
		p.setText(0, 2, description);
		
		p.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				postItemExpand(p, poster, text, date);
				selectedPost = postsTable.getCellForEvent(event).getRowIndex();
				compressNonSelectedPostItems();
			}
		});
		return p;
	}
	
	private void postItemExpand(FlexTable p, String poster, String text, Date date) {
		p.getCellFormatter().setAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_TOP);
		p.setText(0, 3, "by: " + poster);
		p.setText(1, 2, text);
		p.getCellFormatter().setAlignment(1, 3, 
				HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_BOTTOM);
		p.setText(1, 3, date.toString());
		p.getColumnFormatter().setWidth(2, "55%");
		p.getColumnFormatter().setWidth(3, "20%");
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
        			p.removeRow(1);
        		} catch (Exception e) {}
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
		        public void onSuccess(ArrayList<Post> result) {
		        	updatePostList(result);
		        }
		});
	}
	
	public void newPostDialog() {
		newPostDialog.show();
		newPostDialog.center();
		newPostDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				//Window.alert("newPostDialog closed");
				initPosts();
			}
		});
	}
}