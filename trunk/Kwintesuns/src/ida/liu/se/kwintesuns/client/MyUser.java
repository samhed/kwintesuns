package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity public class MyUser implements IsSerializable {
	
	@Id private String id;
	private ArrayList<String> subscriptionList;
	private boolean administrator;

	public MyUser() {}
	
	// should only be called from the serverside
	public MyUser(String i, boolean admin) {
		this.id = i;
		this.administrator = admin;
		this.subscriptionList = new ArrayList<String>();
		//TODO temporary:
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

	public String getFederatedId() {
		return this.id;
	}

	public ArrayList<String> getSubscriptionList() {
		return subscriptionList;
	}
}