package ida.liu.se.kwintesuns.client;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
public class Post implements IsSerializable {
	
	@Id
	private String poster;
	private String title;
	private String type;
	private String description;
	private String picture;
	private String text;
	private Date date;

	public Post() {}
	
	public Post(String ttl, String typ, String descr, 
			String pic, String txt) {
		this.title = ttl;
		this.type = typ;
		this.description = descr;
		this.picture = pic;
		this.text = txt;
		//TODO: consider setting the date of the post in the storePost function instead
		this.date = new Date();
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

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getType() {
		return type;
	}
}
