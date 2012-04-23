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
	private Long commentId;

	public Comment() {}

	// should only be called from the serverside
	public Comment(String txt, Long cid) {
		this.text = txt;
		this.commentId = cid;
	}

	// should only be called from the serverside
	public void setDate(Date date) {
		this.date = date;
	}

	// should only be called from the serverside
	public void setPoster(String poster) {
		this.poster = poster;
	}

	// should only be called from the serverside
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
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

	public Long getCommentId() {
		return commentId;
	}
}
