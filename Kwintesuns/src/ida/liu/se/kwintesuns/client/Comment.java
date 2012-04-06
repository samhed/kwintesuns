package ida.liu.se.kwintesuns.client;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
public class Comment implements IsSerializable {
	
	@Id
	Long id;

	// poster and date is set when the post is stored in the database
	private String poster;
	private String text;
	private Date date;
	private Long postId;

	public Comment() {}
	
	public Comment(String txt, Long pid) {
		this.text = txt;
		this.postId = pid;
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

	public Long getPostId() {
		return postId;
	}
}
