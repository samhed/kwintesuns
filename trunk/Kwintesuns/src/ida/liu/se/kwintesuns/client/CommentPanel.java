package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {
	
	static class tBox {
		public TextArea t = new TextArea();
	}

	public CommentPanel() {
		
		final tBox tB = new tBox();
		tB.t.setText("Write a new comment");
		tB.t.setSize("100%", "500px");
		tB.t.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tB.t.setText("");
			}
		});
		tB.t.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				tB.t.setText("Write a new comment");
			}
		});
		
		add(tB.t);
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHeight("100%");
		setWidth("100%");
	}
}
