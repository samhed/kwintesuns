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
	
	private String watermark;
	private int charLimit = 300;
	private boolean withinLimit = true;

	private HandlerRegistration blurHandler;
	private HandlerRegistration focusHandler;
	
	// Constructors:
	public WatermarkedTextArea() {super();}	
	public WatermarkedTextArea(String defaultValue) {this();setText(defaultValue);}	
	public WatermarkedTextArea(String defaultValue, String watermark) {
		this(defaultValue);
		setWatermark(watermark);
	}
	
	/**
	 * Setter for the charLimit.
	 * @param charLimit
	 */
	public void setCharLimit(int charLimit) {
		this.charLimit = charLimit;
	}
	
	/**
	 * Sets a sort of default text to the textArea which is removed
	 * when the user clicks it to start writing (a watermark).
	 * @param watermark the string to be displayed as watermark
	 */
	public void setWatermark(final String watermark) {
		this.watermark = watermark;
		
		if ((watermark != null) && (watermark != "")) {
			blurHandler = addBlurHandler(this);
			focusHandler = addFocusHandler(this);
			refreshTextArea();
		} else {
			// Remove handlers
			blurHandler.removeHandler();
			focusHandler.removeHandler();
		}
	}
	
	/**
	 * Checks if the number of characters in this
	 * textArea is within its limits.
	 * @return a boolean
	 */
	public boolean isWithinLimit() {
		return withinLimit;
	}
	
	/**
	 * Checks if this textArea is empty which it is 
	 * if it only contains the watermark or is null.
	 * @return a boolean
	 */
	public boolean isEmpty() {
		return (getText().length() == 0) || (getText().equalsIgnoreCase(watermark));
	}

	// We override the onBlur, onFocus and onChange functions
	// in order to specify ourselves what will happen.
	@Override
	public void onBlur(BlurEvent event) {
		refreshTextArea();
		checkIfLimitIsReached();
	}
	@Override
	public void onFocus(FocusEvent event) {
		removeStyleName("watermark");		
		if (getText().equalsIgnoreCase(watermark)) {
			// Hide watermark
			setText("");
		}
		checkIfLimitIsReached();
	}
	@Override
	public void onChange(ChangeEvent event) {
		checkIfLimitIsReached();
	}
	
	/**
	 * Check if the length of the text in the TextArea
	 * has reached the specified limit and set the
	 * withinLimit variable. 
	 */
	private void checkIfLimitIsReached()
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
	
	/**
	 * Show the watermark if the TextArea is empty.
	 */
	private void refreshTextArea() {
		if (isEmpty()) {
			setText(watermark);
			addStyleName("watermark");
		}
	}
}
