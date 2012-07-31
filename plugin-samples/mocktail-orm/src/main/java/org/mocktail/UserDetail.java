package org.mocktail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserDetail {

	@Id @GeneratedValue @Column(name = "MESSAGE_ID")
	private Long id;

	@Column
	private String name;

	public UserDetail(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return name;
	}

	public void setText(String name) {
		this.name = name;
	}

	
}