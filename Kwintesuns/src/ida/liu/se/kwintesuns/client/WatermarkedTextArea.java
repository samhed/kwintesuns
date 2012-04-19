package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

public class WatermarkedTextArea extends TextArea implements BlurHandler, FocusHandler {
	String watermark;
	HandlerRegistration blurHandler;
	HandlerRegistration focusHandler;
	
	public WatermarkedTextArea()	{
		super();
	}
	
	public WatermarkedTextArea(String defaultValue) {
		this();
		setText(defaultValue);
	}
	
	public WatermarkedTextArea(String defaultValue, String watermark) {
		this(defaultValue);
		setWatermark(watermark);
	}
	
	/**
	 * Adds a watermark if the parameter is not NULL or EMPTY
	 * 
	 * @param watermark
	 */
	public void setWatermark(final String watermark) {
		this.watermark = watermark;
		
		if ((watermark != null) && (watermark != "")) {
			blurHandler = addBlurHandler(this);
			focusHandler = addFocusHandler(this);
			EnableWatermark();
		} else {
			// Remove handlers
			blurHandler.removeHandler();
			focusHandler.removeHandler();
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		EnableWatermark();
	}
	
	void EnableWatermark() {
		String text = getText(); 
		if ((text.length() == 0) || (text.equalsIgnoreCase(watermark))) {
			// Show watermark
			setText(watermark);
			addStyleName("watermark");
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		removeStyleName("watermark");		
		if (getText().equalsIgnoreCase(watermark)) {
			// Hide watermark
			setText("");
		}
	}
}
