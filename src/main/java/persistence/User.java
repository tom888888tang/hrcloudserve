package persistence;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
@NamedQuery(name = "AllUsers", query = "select u from User u")

public class User {

	@Id
	private String user_id;
	@Basic
	private String password;
	@Basic
	private String status;
	@Basic
	private String lastname;
	@Basic
	private String firstname;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWechat_id() {
		return wechat_id;
	}
	public void setWechat_id(String wechat_id) {
		this.wechat_id = wechat_id;
	}

	public String getSf_learning_user() {
		return sf_learning_user;
	}
	public void setSf_learning_user(String sf_learning_user) {
		this.sf_learning_user = sf_learning_user;
	}
	public String getSf_learning_user_type() {
		return sf_learning_user_type;
	}
	public void setSf_learning_user_type(String sf_learning_user_type) {
		this.sf_learning_user_type = sf_learning_user_type;
	}

	@Basic
	private String mobile;
	@Basic
	private String email;
	@Basic
	private String wechat_id;
	@Basic
	private String sf_learning_user;
	@Basic
	private String sf_learning_user_type;
	@Basic
	private String learning_score_group;
	@Basic
	private String wechat_nickname;	
	@Basic
	private String wechat_img;
	@Basic
	private String position;
	@Basic
	private String department;
	@Basic
	private String manager;	
	@Basic
	private String region;		
	
	
	

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getWechat_nickname() {
		return wechat_nickname;
	}
	public void setWechat_nickname(String wechat_nickname) {
		this.wechat_nickname = wechat_nickname;
	}
	public String getWechat_img() {
		return wechat_img;
	}
	public void setWechat_img(String wechat_img) {
		this.wechat_img = wechat_img;
	}
	public String getLearning_score_group() {
		return learning_score_group;
	}
	public void setLearning_score_group(String learning_score_group) {
		this.learning_score_group = learning_score_group;
	}	
	

}
