package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Kwintesuns implements EntryPoint {
	
	private FlexTable mainPanel = new FlexTable();
	private CommentPanel commentPanel = new CommentPanel();
	private PostsPanel postsPanel = new PostsPanel(commentPanel);
	private final TopPanel topPanel = new TopPanel(postsPanel);
	private FlexTable contentGrid = new FlexTable();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
	    Window.enableScrolling(false);
	    Window.setMargin("0px");
		
		mainPanel.setSize("100%", "100%");
		mainPanel.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_BOTTOM);
		mainPanel.getCellFormatter().setHeight(0, 0, "90px");
		mainPanel.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
		mainPanel.setCellSpacing(0);
		
		contentGrid.setSize("100%", "100%");
		contentGrid.getFlexCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		contentGrid.getFlexCellFormatter().setAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		contentGrid.getColumnFormatter().setWidth(0, "50%");
		
		contentGrid.setWidget(0, 0, postsPanel);
		contentGrid.setWidget(0, 1, commentPanel);
		
		mainPanel.setWidget(0,0, topPanel);
		mainPanel.setWidget(1,0, contentGrid);
		
		RootLayoutPanel.get().setStyleName("bgStyle");
		RootLayoutPanel.get().add(mainPanel);
		
		postsPanel.initPosts();

		Timer t = new Timer(){
            public void run() {
                topPanel.refresh();
            }
        };
        // schedule the timer to fire every second
        t.scheduleRepeating(1000);
	}
}