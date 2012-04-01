package ida.liu.se.kwintesuns.client;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

@SuppressWarnings("serial")
@Entity
public class MyUser implements Serializable {
	
	@Id
	private String id;
	private ArrayList<String> friendList;
	
	public MyUser() {}
	
	public MyUser(String i) {
		this.id = i;
	}

	public String getFederatedId() {
		return this.id;
	}

	public ArrayList<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<String> friendList) {
		this.friendList = friendList;
	}
}
