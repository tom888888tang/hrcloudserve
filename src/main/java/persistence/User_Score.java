package persistence;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "USER_SCORE")
@IdClass(User_Score_Key.class)
public class User_Score {

	@Id
	private String user_id;
	@Id
	private String course_id;	
	@Id
	private String course_version;		
	@Basic
	private String score;
	@Basic
	private String title;	
	@Basic
	private String course_type;	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCourse_id() {
		return course_id;
	}
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	public String getCourse_version() {
		return course_version;
	}
	public void setCourse_version(String course_version) {
		this.course_version = course_version;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCourse_type() {
		return course_type;
	}
	public void setCourse_type(String course_type) {
		this.course_type = course_type;
	}

	

}
