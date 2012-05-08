package ida.liu.se.kwintesuns.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

public class WatermarkedTextArea extends TextArea implements BlurHandler, FocusHandler, ChangeHandler {
	String watermark;
	int charLimit = 300;
	boolean withinLimit = true;

	HandlerRegistration blurHandler;
	HandlerRegistration focusHandler;
	HandlerRegistration keyPressHandler;
	
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

	public boolean isWithinLimit() {
		return withinLimit;
	}
	
	public void setCharLimit(int charLimit) {
		this.charLimit = charLimit;
	}
	
	public boolean isEmpty() {
		return (getText().length() == 0) || 
				(getText().equalsIgnoreCase(watermark));
	}
	
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
		onTextAreaContentChanged();
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
		onTextAreaContentChanged();
	}

	@Override
	public void onChange(ChangeEvent event) {
		  onTextAreaContentChanged();
	}
	
	private void onTextAreaContentChanged()
    {
		int counter = new Integer(getText().length()).intValue();
		int charsRemaining = charLimit - counter;
		if (charsRemaining >= 0) {
			removeStyleName("redBG");
			withinLimit = true;
		} else {
			setStyleName("redBG");
			withinLimit = false;
		}
    }
}
