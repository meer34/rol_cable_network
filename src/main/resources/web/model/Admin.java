package com.hunter.web.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@EntityListeners(DeleteEventListener.class)
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String name;
	private String phone;
	private String address;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id", unique=true)
	private User user;
	
}
