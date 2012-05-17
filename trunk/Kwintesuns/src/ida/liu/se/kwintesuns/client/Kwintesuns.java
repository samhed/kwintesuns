package ida.liu.se.kwintesuns.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Kwintesuns implements EntryPoint {
	
	private FlexTable mainPanel = new FlexTable();
	private ContentPanel contentPanel = new ContentPanel();
	private final TopPanel topPanel = new TopPanel(contentPanel);

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
				
		mainPanel.setWidget(0,0, topPanel);
		mainPanel.setWidget(1,0, contentPanel);
		
		RootLayoutPanel.get().setStyleName("bgStyle");
		RootLayoutPanel.get().add(mainPanel);
		
        topPanel.init();
		contentPanel.init();
	}
}