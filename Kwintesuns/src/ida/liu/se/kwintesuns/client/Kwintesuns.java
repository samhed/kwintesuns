package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Kwintesuns implements EntryPoint {
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final FlexTable contentGrid = new FlexTable();
	private PostsPanel postsPanel = new PostsPanel();
	private TopPanel topPanel = new TopPanel(postsPanel);
	private CommentPanel commentPanel = new CommentPanel();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		contentGrid.setSize("100%", "100%");
		contentGrid.getFlexCellFormatter().setAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, 
				HasVerticalAlignment.ALIGN_TOP);
		contentGrid.setCellSpacing(0);
		contentGrid.getColumnFormatter().setWidth(0, "50%");
		contentGrid.getColumnFormatter().setWidth(1, "50%");
		
		contentGrid.setWidget(0, 0, postsPanel);
		contentGrid.setWidget(0, 1, commentPanel);
		
		mainPanel.setWidth("100%");
		//mainPanel.setHeight("200px");
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		mainPanel.add(topPanel);
		mainPanel.add(contentGrid);
		
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