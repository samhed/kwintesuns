package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity public class Post implements IsSerializable {
	
	@Id	Long id;
	
	private String author;
	private String title;
	private String type;
	private String description;
	private String thumbnail;
	private String text;
	private String update;
	private Date date;
	private ArrayList<String> flagList;

	public Post() {}

	// The poster and date is set when the post is stored in the database
	public Post(String ttl, String typ, String descr, 
			String thumb, String txt) {
		this.title = ttl;
		this.type = typ;
		this.description = descr;
		this.thumbnail = thumb;
		this.text = txt;
		this.flagList = new ArrayList<String>();
		this.flagList.add("test@example.com");
	}
	
	// Should only be called from the serverside:
	public void addToFlagList(String flagger) {this.flagList.add(flagger);}
	public void setDate(Date date) {this.date = date;}
	public void setAuthor(String author) {this.author = author;}
	public void setUpdate(String update) {this.update = update;}

	// Should only be used when making a temporary post:
	public void addToText(String text) {this.text = this.text + text;}
	public void setText(String text) {this.text = text;}
	public void setId(Long id) {this.id = id;}

	// Getters:
	public Long getId() {return id;}
	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public String getThumbnail() {return thumbnail;}
	public String getText() {return text;}
	public Date getDate() {return date;}
	public String getAuthor() {return author;}	
	public String getType() {return type;}
	public String getUpdate() {return update;}
	public ArrayList<String> getFlagList() {return flagList;}
}
