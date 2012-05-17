package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity public class Post implements IsSerializable {
	
	@Id	Long id;
	
	// poster and date is set when the post is stored in the database
	private String author;
	private String title;
	private String type;
	private String description;
	private String picture;
	private String text;
	private String update;
	private Date date;
	private ArrayList<String> flagList;

	public Post() {}

	// should only be called from the serverside
	public Post(String ttl, String typ, String descr, 
			String pic, String txt) {
		this.title = ttl;
		this.type = typ;
		this.description = descr;
		this.picture = pic;
		this.text = txt;
	}

	// should only be called from the serverside
	public void setDate(Date date) {
		this.date = date;
	}

	// should only be called from the serverside
	public void setAuthor(String author) {
		this.author = author;
	}

	// should only be called from the serverside
	public void setUpdate(String update) {
		this.update = update;
	}

	// should only be used when making a temporary post
	public void setId(Long id) {
		this.id = id;
	}

	// should only be called from the serverside
	public void setFlagList(ArrayList<String> flagList) {
		this.flagList = flagList;
	}
	
	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPicture() {
		return picture;
	}

	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}

	public String getAuthor() {
		return author;
	}
	
	public String getType() {
		return type;
	}

	public String getUpdate() {
		return update;
	}

	public ArrayList<String> getFlagList() {
		return flagList;
	}
}
