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
	
	public void setDate(Date date) {
		this.date = date;
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
