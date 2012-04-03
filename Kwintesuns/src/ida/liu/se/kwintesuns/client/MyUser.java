package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
public class MyUser implements IsSerializable {
	
	@Id 	
	private String id;
	private ArrayList<String> friendList;
	
	public MyUser() {}
	
	public MyUser(String i) {
		this.id = i;
		this.friendList = new ArrayList<String>();
		this.friendList.add("test@example.com");
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