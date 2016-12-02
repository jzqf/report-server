package com.qfree.bo.report.rest.client;

import java.io.Serializable;

public class UserTest implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String userName;
	private String email;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserTest [id=" + id + ", name=" + name + ", userName=" + userName + ", email=" + email + "]";
	}

}
