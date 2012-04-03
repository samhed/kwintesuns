package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class PostsPanel extends ScrollPanel{
	
	private FlexTable postsTable = new FlexTable();
	private final NewPostDialog newPostDialog = new NewPostDialog();
	private final MyUserServiceAsync async = GWT.create(MyUserService.class);
	private ArrayList<Post> postList;
	
	public PostsPanel() {
		add(postsTable);
		
		setHeight("100%");
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
		postList = result;
		int row = 0;
        postsTable.removeAllRows();
        postsTable.setText(0, 0, "Type");
        postsTable.setText(0, 1, "Date");
        postsTable.setText(0, 2, "Title");
        postsTable.setText(0, 3, "Poster");
        postsTable.setText(0, 4, "Text");
        //loop the array list and user getters to add 
        //records to the table
        for (Post post : postList) {
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
	
	public void newPost() {
		newPostDialog.show();
		newPostDialog.center();
	}

}