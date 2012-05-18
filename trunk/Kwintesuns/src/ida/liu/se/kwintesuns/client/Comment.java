package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity public class Comment implements IsSerializable {
	
	@Id	private Long id;

	private String author;
	private String text;
	private Date date;
	private Long postId;
	private ArrayList<String> flagList;

	public Comment() {}

	// The poster and date is set when the post is stored in the database
	public Comment(String txt, Long pid) {
		this.text = txt;
		this.postId = pid;
		this.flagList = new ArrayList<String>();
		this.flagList.add("test@example.com");
	}

	// Should only be called from the serverside:
	public void addToFlagList(String flagger) {this.flagList.add(flagger);}
	public void setDate(Date date) {this.date = date;}
	public void setAuthor(String author) {this.author = author;}
	public void setPostId(Long commentId) {this.postId = commentId;}
	
	// Getters:
	public String getText() {return text;}
	public Date getDate() {return date;}
	public String getAuthor() {return author;}
	public Long getPostId() {return postId;}
	public Long getId() {return id;}
	public ArrayList<String> getFlagList() {return flagList;}
}
