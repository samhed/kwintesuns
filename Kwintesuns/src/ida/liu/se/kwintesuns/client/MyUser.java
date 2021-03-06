package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity public class MyUser implements IsSerializable {
	
	@Id private String email;
	private ArrayList<String> subscriptionList;
	private boolean administrator;

	public MyUser() {}
	
	// should only be called from the serverside
	public MyUser(String i, boolean admin) {
		this.email = i;
		this.administrator = admin;
		this.subscriptionList = new ArrayList<String>();
		this.addSubscription("test@example.com");
	}

	// should only be called from the serverside
	public void addSubscription(String emailToSubscribeTo) {
		this.subscriptionList.add(emailToSubscribeTo);
	}

	// should only be called from the serverside
	public void removeSubscription(String emailToUnsubscribeFrom) {
		this.subscriptionList.remove(emailToUnsubscribeFrom);
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public String getEmail() {
		return this.email;
	}

	public ArrayList<String> getSubscriptionList() {
		return subscriptionList;
	}
}