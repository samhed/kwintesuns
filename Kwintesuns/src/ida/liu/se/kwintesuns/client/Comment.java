package ida.liu.se.kwintesuns.client;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
public class Comment implements IsSerializable {
	
	@Id
	private Long id;

	// poster and date is set when the post is stored in the database
	private String author;
	private String text;
	private Date date;
	private Long postId;

	public Comment() {}

	// should only be called from the serverside
	public Comment(String txt, Long pid) {
		this.text = txt;
		this.postId = pid;
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
	public void setPostId(Long commentId) {
		this.postId = commentId;
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

	public Long getPostId() {
		return postId;
	}

	public Long getId() {
		return id;
	}
}
