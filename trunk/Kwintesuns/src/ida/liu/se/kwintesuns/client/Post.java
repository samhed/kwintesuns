package ida.liu.se.kwintesuns.client;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
public class Post implements IsSerializable {
	
	@Id
	Long id;
	
	// poster and date is set when the post is stored in the database
	private String author;
	private String title;
	private String type;
	private String description;
	private String picture;
	private String text;
	private String update;
	private Date date;

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
	public void setPoster(String poster) {
		this.author = poster;
	}

	// should only be called from the serverside
	public void setUpdate(String update) {
		this.update = update;
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
}
